import { getBelimoAccessToken } from '@/lib/belimo';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function fetchLifetimeAt(
  deviceId: string,
  isoTimestamp: string,
  token: string
): Promise<number | null> {
  const url = `https://cloud.belimo.com/api/v3/devices/${deviceId}/data?datapointIds=HeatingEnergyLifetime&at=${isoTimestamp}`;
  const res = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) {
    console.warn(
      `Failed to fetch at ${isoTimestamp} for ${deviceId}: ${res.status}`
    );
    return null;
  }

  const json = await res.json();
  const datapoint = json?.datapoints?.HeatingEnergyLifetime;

  if (!datapoint || typeof datapoint.value !== 'number') {
    return null;
  }

  return datapoint.value;
}

export async function GET(request: Request) {
  const authHeader = request.headers.get('Authorization');
  if (authHeader !== `Bearer ${process.env.CRON_SECRET}`) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const token = await getBelimoAccessToken();
    if (!token) return new Response('Missing token', { status: 401 });

    const now = new Date();
    const from = new Date(
      Date.UTC(now.getUTCFullYear(), now.getUTCMonth() - 1, 1)
    );
    const to = new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1));

    const fromISO = from.toISOString();
    const toISO = to.toISOString();

    const devices = await prisma.device.findMany({
      where: { source: 'BELIMO' },
      include: { building: true },
    });

    for (const device of devices) {
      const fromValue = await fetchLifetimeAt(
        device.externalId,
        fromISO,
        token
      );
      const toValue = await fetchLifetimeAt(device.externalId, toISO, token);

      if (toValue === null) {
        console.warn(`Missing 'to' value for ${device.externalId}, skipping.`);
        continue;
      }

      const deltaJoules = fromValue !== null ? toValue - fromValue : toValue;
      const deltaKWh = deltaJoules / 3_600_000;

      if (deltaKWh < 0) {
        console.warn(`Negative delta for ${device.externalId}, skipping.`);
        continue;
      }

      await prisma.buildingData.upsert({
        where: {
          buildingId_type_date: {
            buildingId: device.buildingId,
            type: 'ACTUAL',
            date: from,
          },
        },
        update: {
          spaceHeatingkWh: deltaKWh,
          totalEnergykWh: {
            increment: deltaKWh,
          },
        },
        create: {
          buildingId: device.buildingId,
          type: 'ACTUAL',
          date: from,
          spaceHeatingkWh: deltaKWh,
          totalEnergykWh: deltaKWh,
        },
      });
    }

    return new Response('Belimo cron completed');
  } catch (error) {
    console.error('Cron error:', error);
    return new Response('Internal error', { status: 500 });
  }
}

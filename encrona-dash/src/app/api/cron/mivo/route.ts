import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export async function POST(request: Request) {
  const authHeader = request.headers.get('Authorization');

  if (!authHeader?.startsWith('Bearer ')) {
    return new Response('Unauthorized: missing or invalid header', {
      status: 401,
    });
  }

  const token = authHeader.split(' ')[1];
  if (token !== process.env.MIVO_TOKEN) {
    return new Response('Forbidden: invalid token', { status: 403 });
  }

  try {
    const body = await request.json();
    const unitSerial = body?.UnitSerial;
    const readings = body?.Readings;

    if (!unitSerial || !Array.isArray(readings)) {
      return new Response('Bad Request: missing UnitSerial or Readings', {
        status: 400,
      });
    }

    const device = await prisma.device.findFirst({
      where: {
        externalId: unitSerial,
        source: 'MIVO',
      },
      include: {
        building: true,
      },
    });

    if (!device || !device.building) {
      return new Response(`No building linked to device ${unitSerial}`, {
        status: 404,
      });
    }

    const buildingId = device.building.id;
    const now = new Date();
    const monthStart = new Date(
      Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1)
    );

    let totalElectricity = 0;
    let totalWater = 0;
    let electricityCount = 0;
    let waterCount = 0;

    for (const reading of readings) {
      for (const value of reading.Values ?? []) {
        if (value.Code === 'ElectricityImportedActiveEnergyTotal') {
          totalElectricity += (value.Value ?? 0) / 1000;
          electricityCount++;
        }
        if (value.Code === 'WaterVolume') {
          totalWater += value.Value ?? 0;
          waterCount++;
        }
      }
    }

    console.log(
      `Electricity readings: ${electricityCount}, Water readings: ${waterCount}`
    );

    const previousTotals = await prisma.buildingData.aggregate({
      where: {
        buildingId,
        type: 'ACTUAL',
        date: { lte: monthStart },
      },
      _sum: {
        electricitykWh: true,
        totalWaterM3: true,
      },
    });

    const summedElectricity = previousTotals._sum.electricitykWh ?? 0;
    const summedWater = previousTotals._sum.totalWaterM3 ?? 0;

    if (totalElectricity < summedElectricity || totalWater < summedWater) {
      return new Response('Skipped: readings lower than existing totals', {
        status: 200,
      });
    }

    const deltaElectricity = totalElectricity - summedElectricity;
    const deltaWater = totalWater - summedWater;

    const existing = await prisma.buildingData.findUnique({
      where: {
        buildingId_type_date: {
          buildingId,
          type: 'ACTUAL',
          date: monthStart,
        },
      },
    });

    if (existing) {
      await prisma.buildingData.update({
        where: {
          buildingId_type_date: {
            buildingId,
            type: 'ACTUAL',
            date: monthStart,
          },
        },
        data: {
          electricitykWh: { increment: deltaElectricity },
          totalWaterM3: { increment: deltaWater },
        },
      });
    } else {
      await prisma.buildingData.create({
        data: {
          buildingId,
          type: 'ACTUAL',
          date: monthStart,
          electricitykWh: deltaElectricity,
          totalWaterM3: deltaWater,
        },
      });
    }

    return new Response(`Data stored for building ${buildingId}`, {
      status: 200,
    });
  } catch (err) {
    console.error('Error in MIVO POST:', err);
    return new Response('Internal Server Error', { status: 500 });
  }
}

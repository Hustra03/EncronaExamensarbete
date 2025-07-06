import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

const months = [
  'january',
  'february',
  'march',
  'april',
  'may',
  'june',
  'july',
  'august',
  'september',
  'october',
  'november',
  'december',
] as const;

export async function POST(req: NextRequest) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const body = await req.json();
    const { buildingId, type, energyType, values } = body;

    if (!buildingId || !type || !energyType || !values) {
      return new Response('Missing required fields', { status: 400 });
    }

    const monthlyValues: Record<string, number> = {};
    for (let i = 0; i < 12; i++) {
      const key = months[i];
      const value = parseFloat(values[i + 1]);
      monthlyValues[key] = isNaN(value) ? 0 : value;
    }

    const result = await prisma.benchmark.upsert({
      where: {
        buildingId_type_energyType: {
          buildingId,
          type,
          energyType,
        },
      },
      create: {
        building: { connect: { id: buildingId } },
        type,
        energyType,
        ...monthlyValues,
      },
      update: {
        ...monthlyValues,
      },
    });

    if (
      ['SPACE_HEATING', 'WATER_HEATING', 'ELECTRICITY'].includes(energyType)
    ) {
      const parts = await prisma.benchmark.findMany({
        where: {
          buildingId,
          type,
          energyType: {
            in: ['SPACE_HEATING', 'WATER_HEATING', 'ELECTRICITY'],
          },
        },
      });

      const totalValues: Record<string, number> = {};
      for (const month of months) {
        totalValues[month] = parts.reduce((sum, b) => sum + (b[month] ?? 0), 0);
      }

      await prisma.benchmark.upsert({
        where: {
          buildingId_type_energyType: {
            buildingId,
            type,
            energyType: 'TOTAL',
          },
        },
        create: {
          building: { connect: { id: buildingId } },
          type,
          energyType: 'TOTAL',
          ...totalValues,
        },
        update: {
          ...totalValues,
        },
      });
    }

    return new Response(JSON.stringify(result), { status: 200 });
  } catch (error) {
    console.error('Failed to upsert Benchmark', error);
    return new Response('Internal Server Error', { status: 500 });
  }
}

export async function GET(req: NextRequest) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const { searchParams } = new URL(req.url);
  const buildingId = searchParams.get('buildingId');

  if (!buildingId) {
    return new Response('Missing buildingId', { status: 400 });
  }

  const parsedBuildingId = parseInt(buildingId);
  if (isNaN(parsedBuildingId)) {
    return new Response('Invalid buildingId', { status: 400 });
  }

  const benchmarks = await prisma.benchmark.findMany({
    where: {
      buildingId: parsedBuildingId,
    },
    orderBy: {
      type: 'asc',
    },
  });

  return new Response(JSON.stringify(benchmarks), { status: 200 });
}

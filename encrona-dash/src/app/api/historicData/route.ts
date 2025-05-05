import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';
import { isAfter } from 'date-fns';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function POST(req: NextRequest) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const body = await req.json();
    const {
      buildingId,
      date,
      spaceHeatingkWh,
      waterHeatingkWh,
      electricitykWh,
      totalWaterM3,
      spaceHeatingCost,
      waterHeatingCost,
      electricityCost,
      totalWaterCost,
    } = body;

    if (!buildingId || !date) {
      return new Response('Missing buildingId or date', { status: 400 });
    }

    const buildingDate = new Date(date);
    const now = new Date();

    // Validate date must be before today
    if (isAfter(buildingDate, now)) {
      return new Response('Date cannot be in the future', { status: 400 });
    }

    if (
      buildingDate.getUTCDate() !== 1 ||
      buildingDate.getUTCHours() !== 0 ||
      buildingDate.getUTCMinutes() !== 0 ||
      buildingDate.getUTCSeconds() !== 0 ||
      buildingDate.getUTCMilliseconds() !== 0
    ) {
      return new Response(
        'Date must be exactly first of month at midnight UTC',
        { status: 400 }
      );
    }
    const totalEnergykWh = spaceHeatingkWh + waterHeatingkWh + electricitykWh;

    const totalEnergyCost =
      spaceHeatingCost + waterHeatingCost + electricityCost;

    const result = await prisma.buildingData.upsert({
      where: {
        buildingId_type_date: {
          buildingId,
          type: 'ACTUAL',
          date: buildingDate,
        },
      },
      create: {
        buildingId,
        type: 'ACTUAL',
        date: buildingDate,
        totalEnergykWh,
        spaceHeatingkWh,
        waterHeatingkWh,
        electricitykWh,
        totalWaterM3,
        totalEnergyCost,
        spaceHeatingCost,
        waterHeatingCost,
        electricityCost,
        totalWaterCost,
      },
      update: {
        totalEnergykWh,
        spaceHeatingkWh,
        waterHeatingkWh,
        electricitykWh,
        totalWaterM3,
        totalEnergyCost,
        spaceHeatingCost,
        waterHeatingCost,
        electricityCost,
        totalWaterCost,
      },
    });

    return new Response(JSON.stringify(result), { status: 200 });
  } catch (error) {
    console.error('Failed to create/update BuildingData', error);
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
  const year = searchParams.get('year');
  const month = searchParams.get('month');

  if (!buildingId || !year || !month) {
    return new Response('Missing query parameters', { status: 400 });
  }

  const parsedBuildingId = parseInt(buildingId);
  const parsedYear = parseInt(year);
  const parsedMonth = parseInt(month);

  if (isNaN(parsedBuildingId) || isNaN(parsedYear) || isNaN(parsedMonth)) {
    return new Response('Invalid query parameters', { status: 400 });
  }

  const date = new Date(Date.UTC(parsedYear, parsedMonth - 1, 1, 0, 0, 0, 0));

  const existingData = await prisma.buildingData.findFirst({
    where: {
      buildingId: parsedBuildingId,
      type: 'ACTUAL',
      date: date,
    },
  });

  if (!existingData) {
    return new Response(null, { status: 204 });
  }

  return new Response(JSON.stringify(existingData), { status: 200 });
}

import { auth, isAdmin } from '@/lib/auth';
import { recalculateBuildingCosts } from '@/lib/recalculateBuildingCosts';
import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function POST(req: NextRequest) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const body = await req.json();
    const { buildingId, date, type, price } = body;

    if (!buildingId || !date || !type || price == null) {
      return new Response('Missing fields', { status: 400 });
    }

    const dateObj = new Date(date);
    const now = new Date();

    if (
      dateObj.getUTCDate() !== 1 ||
      dateObj.getUTCHours() !== 0 ||
      dateObj.getUTCMinutes() !== 0 ||
      dateObj.getUTCSeconds() !== 0 ||
      dateObj.getUTCMilliseconds() !== 0
    ) {
      return new Response(
        'Date must be the first of the month at 00:00:00 UTC',
        { status: 400 }
      );
    }

    const forecastMonth = new Date(
      Date.UTC(dateObj.getUTCFullYear(), dateObj.getUTCMonth(), 1)
    );
    const currentMonth = new Date(
      Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1)
    );

    if (forecastMonth < currentMonth) {
      return new Response('Cannot submit forecasts for past months', {
        status: 400,
      });
    }

    if (typeof price !== 'number' || isNaN(price) || price < 0) {
      return new Response('Price can not be negative', {
        status: 400,
      });
    }

    const forecast = await prisma.priceForecast.upsert({
      where: {
        buildingId_dateFrom_type: {
          buildingId,
          dateFrom: forecastMonth,
          type,
        },
      },
      update: {
        price,
      },
      create: {
        buildingId,
        dateFrom: forecastMonth,
        type,
        price,
      },
    });

    await recalculateBuildingCosts(buildingId, type);

    return new Response(JSON.stringify(forecast), { status: 200 });
  } catch (err) {
    console.error('Failed to save price forecast', err);
    return new Response('Internal Server Error', { status: 500 });
  }
}

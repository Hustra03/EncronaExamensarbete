import { auth, isAdmin } from '@/lib/auth';
import { recalculateBuildingCosts } from '@/lib/recalculateBuildingCosts';
import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const session = await auth();
    if (!isAdmin(session)) {
      return new Response(JSON.stringify({ error: 'Unauthorized' }), {
        status: 401,
      });
    }

    const { id: idParam } = await params;
    const id = parseInt(idParam);

    if (isNaN(id)) {
      return new Response(JSON.stringify({ error: 'Invalid building ID' }), {
        status: 400,
      });
    }

    const now = new Date();
    const currentMonthStart = new Date(
      Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1)
    );

    const forecasts = await prisma.priceForecast.findMany({
      where: {
        buildingId: id,
        dateFrom: {
          gte: currentMonthStart,
        },
      },
      orderBy: [{ dateFrom: 'asc' }, { type: 'asc' }],
    });

    return new Response(JSON.stringify(forecasts), { status: 200 });
  } catch (err) {
    console.error('Failed to retrieve forecasts:', err);
    return new Response(JSON.stringify({ error: 'Internal Server Error' }), {
      status: 500,
    });
  }
}

export async function DELETE(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const { id: idParam } = await params;
  const id = parseInt(idParam);

  if (isNaN(id)) {
    return new Response('Invalid ID', { status: 400 });
  }

  try {
    const deleted = await prisma.priceForecast.delete({
      where: { id },
    });
    await recalculateBuildingCosts(deleted.buildingId, deleted.type);
    return new Response('Deleted', { status: 200 });
  } catch (error) {
    console.error('Failed to delete price forecast', error);
    return new Response('Not found or already deleted', { status: 404 });
  }
}

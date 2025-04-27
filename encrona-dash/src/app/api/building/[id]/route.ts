import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function GET(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const { id } = await params;
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const buildingId = parseInt(id);
  if (isNaN(buildingId)) {
    return new Response('Invalid building ID', { status: 400 });
  }

  const building = await prisma.building.findUnique({
    where: { id: buildingId },
    select: { name: true, installedAt: true },
  });

  if (!building) {
    return new Response('Building not found', { status: 404 });
  }

  const actual = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ACTUAL' },
    orderBy: { date: 'asc' },
  });

  const estimate = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ESTIMATE' },
    orderBy: { date: 'asc' },
  });

  return new Response(
    JSON.stringify({
      building,
      actual,
      estimate,
    }),
    { status: 200 }
  );
}

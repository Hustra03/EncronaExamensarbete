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
  const encoder = new TextEncoder();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const buildingId = parseInt(id);
  if (isNaN(buildingId)) {
    return new Response('Invalid building ID', { status: 400 });
  }

  const building = await prisma.building.findUnique({
    where: { id: buildingId },
    select: { name: true, owner: true, installedAt: true },
  });

  if (!building) {
    return new Response('Building not found', { status: 404 });
  }

  const { readable, writable } = new TransformStream();
  const writer = writable.getWriter();

  /* eslint-disable  @typescript-eslint/no-explicit-any */
  const writeSSE = (event: string, data: any) => {
    writer.write(
      encoder.encode(`event: ${event}\ndata: ${JSON.stringify(data)}\n\n`)
    );
  };

  const actual = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ACTUAL' },
    orderBy: { date: 'asc' },
  });

  const estimate = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ESTIMATE' },
    orderBy: { date: 'asc' },
  });

  writeSSE('actual', {
    building,
    data: actual,
  });

  {
    /*
  const now = new Date();
  const cutoffDate = startOfMonth(addMonths(now, 12));

  const lastEstimate = estimate.at(-1);

  const isMissing = !lastEstimate || lastEstimate.date < cutoffDate;

  Do recalculations here if missing months or outside of range
  */
  }
  writeSSE('estimate', estimate);
  writer.close();

  return new Response(readable, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection':'close'
    },
  });
}

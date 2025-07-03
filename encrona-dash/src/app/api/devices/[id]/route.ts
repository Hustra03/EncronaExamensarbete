import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const { id: idParam } = await params;
  const id = parseInt(idParam);
  if (isNaN(id)) return new Response('Invalid ID', { status: 400 });

  const devices = await prisma.device.findMany({
    where: { buildingId: id },
    select: { id: true, externalId: true, source: true },
  });

  return new Response(JSON.stringify(devices), { status: 200 });
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
  if (isNaN(id)) return new Response('Invalid ID', { status: 400 });

  await prisma.device.delete({ where: { id } });
  return new Response('Deleted', { status: 200 });
}

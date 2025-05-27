import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function POST(req: NextRequest) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const { buildingId, source, externalId } = await req.json();

  if (!buildingId || !source || !externalId) {
    return new Response('Missing fields', { status: 400 });
  }

  const existing = await prisma.device.findFirst({
    where: { buildingId, source },
  });

  if (existing) {
    return new Response(
      'Device of this type already exists for this building',
      {
        status: 400,
      }
    );
  }

  const device = await prisma.device.create({
    data: {
      buildingId,
      externalId,
      source,
    },
  });

  return new Response(JSON.stringify(device), { status: 201 });
}

import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET() {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const buildings = await prisma.building.findMany({
    select: {
      id: true,
      name: true,
      owner: true,
      installedAt: true,
      createdAt: true,
    },
  });

  return new Response(JSON.stringify(buildings), { status: 200 });
}

export async function POST(request: Request) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const body = await request.json();
    const name = body.name;
    const owner = body.owner;
    let installedAt = body.installedAt;

    if (!installedAt || !owner || !name) {
      return new Response(JSON.stringify({ message: 'Missing fields' }), {
        status: 400,
      });
    }

    installedAt = new Date(installedAt).toISOString();
    await prisma.building.create({
      data: {
        installedAt,
        owner,
        name,
      },
    });

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error creating building:', err);
    return new Response(JSON.stringify({ message: 'Server error' }), {
      status: 500,
    });
  }
}

import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET() {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const companies = await prisma.company.findMany({
    select: {
      id: true,
      name: true,
      owner: true,
      createdAt: true,
    },
  });

  return new Response(JSON.stringify(companies), { status: 200 });
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

    if (!owner || !name) {
      return new Response(JSON.stringify({ message: 'Missing fields' }), {
        status: 400,
      });
    }

    await prisma.company.create({
      data: {
        owner,
        name,
      },
    });

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error creating company:', err);
    return new Response(JSON.stringify({ message: 'Server error' }), {
      status: 500,
    });
  }
}

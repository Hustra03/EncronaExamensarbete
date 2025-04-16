import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function PUT(
  request: Request,
  { params }: { params: { id: string } }
) {
  const session = await auth();

  if (!session || !isAdmin(session)) {
    return new Response(JSON.stringify({ message: 'Unauthorized' }), {
      status: 401,
    });
  }

  const id = parseInt(params.id);
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

  try {
    await prisma.building.update({
      where: { id },
      data: {
        name,
        owner,
        installedAt,
      },
    });

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error editing building:', err);
    return new Response(
      JSON.stringify({ message: 'Building not found or error' }),
      { status: 400 }
    );
  }
}

export async function DELETE(
  _: Request,
  { params }: { params: { id: string } }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const id = parseInt(params.id);

  try {
    await prisma.building.delete({ where: { id } });
    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error deleting building:', err);
    return new Response(
      JSON.stringify({ message: 'Building not found or error' }),
      {
        status: 400,
      }
    );
  }
}

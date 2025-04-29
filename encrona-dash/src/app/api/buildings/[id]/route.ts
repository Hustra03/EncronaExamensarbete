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
  const companiesWithAccess = body.companiesWithAccess;

  let installedAt = body.installedAt;

  if (!installedAt || !owner || !name || !companiesWithAccess) {
    return new Response(JSON.stringify({ message: 'Missing fields' }), {
      status: 400,
    });
  }

  if (companiesWithAccess.length == 0) {
    return new Response(JSON.stringify({ message: 'No company specified' }), {
      status: 400,
    });
  }

  const companiesWithAccessTyped: number[] = companiesWithAccess;

  installedAt = new Date(installedAt).toISOString();

  try {
    await prisma.building.update({
      where: { id },
      data: {
        name,
        owner,
        installedAt,
        companiesWithAccess: {
          set: [], //This removes any existing relations
          connect: companiesWithAccessTyped.map(id => {
            return { id: id };
          }), //And this adds the relations which were specified now, so only they will now exist
        },
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

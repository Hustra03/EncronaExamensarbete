import bcrypt from 'bcryptjs';
import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET(
  request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }
  const { id: idParam } = await params;
  const id = parseInt(idParam);

  const users = await prisma.user.findMany({
    where: {
      companyId: id,
    },
    select: {
      id: true,
      name: true,
      email: true,
      role: true,
      company: {
        select: {
          name: true,
        },
      },
      createdAt: true,
    },
  });

  return new Response(JSON.stringify(users), { status: 200 });
}

export async function PUT(
  request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();
  const { id: idParam } = await params;

  if (!session || (!isAdmin(session) && session.user.id !== idParam)) {
    return new Response(JSON.stringify({ message: 'Unauthorized' }), {
      status: 401,
    });
  }

  const id = parseInt(idParam);
  const body = await request.json();
  const { email, name, role, password, companyId } = body;

  if (!email || !name || !role || !password) {
    return new Response(
      JSON.stringify({ message: 'Missing required fields' }),
      { status: 400 }
    );
  }

  try {
    const hashedPassword = await bcrypt.hash(password, 10);

    await prisma.user.update({
      where: { id },
      data: {
        email,
        name,
        password: hashedPassword,
        ...(isAdmin(session) && role ? { role } : {}), // Only update role if admin
      },
    });

    if (companyId) {
      await prisma.user.update({
        where: { id },
        data: {
          email,
          name,
          password: hashedPassword,
          ...(isAdmin(session) && role ? { role } : {}), // Only update role if admin
          company: { connect: { id: companyId } },
        },
      });
    } else {
      await prisma.user.update({
        where: { id },
        data: {
          email,
          name,
          password: hashedPassword,
          ...(isAdmin(session) && role ? { role } : {}), // Only update role if admin
        },
      });
    }

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error editing user:', err);
    return new Response(
      JSON.stringify({ message: 'User not found or error' }),
      { status: 400 }
    );
  }
}

export async function DELETE(
  _: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const { id: idParam } = await params;
  const id = parseInt(idParam);

  try {
    await prisma.user.delete({ where: { id } });
    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error deleting user:', err);
    return new Response(
      JSON.stringify({ message: 'User not found or error' }),
      {
        status: 400,
      }
    );
  }
}

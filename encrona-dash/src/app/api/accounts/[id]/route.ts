import bcrypt from 'bcryptjs';
import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function PUT(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  const { id:idParam } = await params;

  const session = await auth();
  if (!session || (!isAdmin(session) && session.user.id !== params.id)) {
    return new Response(JSON.stringify({ message: 'Unauthorized' }), {
      status: 401,
    });
  }

  const id = parseInt(idParam);
  const body = await req.json();
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
  { params }: { params: { id: string } }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const id = parseInt(params.id);

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

import bcrypt from 'bcryptjs';
import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function PUT(
  request: NextRequest,
  { params }: { params: { id: string } }
) {
  const { id: idParam } = await params;
  const id = parseInt(idParam);

  const session = await auth();
  if (!session || (!isAdmin(session) && session.user.id !== idParam)) {
    return new Response(JSON.stringify({ message: 'Unauthorized' }), {
      status: 401,
    });
  }

  const body = await request.json();
  const { email, name, role, password, companyId } = body;

  try {
    let hashedPassword = '';
    if (password.length != 0) {
      hashedPassword = await bcrypt.hash(password, 10);
    }

    if (companyId) {
      await prisma.user.update({
        where: { id },
        data: {
          email: email != '' ? email : undefined, //If email is '' then set to undefined => do not update
          name: name != '' && name != undefined ? name : undefined,
          password:
            hashedPassword != '' && hashedPassword != undefined
              ? hashedPassword
              : undefined,
          ...(isAdmin(session) && role ? { role } : {}), // Only update role if admin
          company: { connect: { id: companyId } },
        },
      });
    } else {
      await prisma.user.update({
        where: { id },
        data: {
          email: email != '' ? email : undefined, //If email is '' then set to undefined => do not update
          name: name != '' && name != undefined ? name : undefined,
          password:
            hashedPassword != '' && hashedPassword != undefined
              ? hashedPassword
              : undefined,
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
  const { id: idParam } = await params;

  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

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

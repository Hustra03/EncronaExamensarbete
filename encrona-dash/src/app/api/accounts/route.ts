import { hash } from 'bcryptjs';
import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET() {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const users = await prisma.user.findMany({
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

export async function POST(request: Request) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const { email, password, name, role, companyId } = await request.json();

    if (!email || !password || !name || !role) {
      return new Response(JSON.stringify({ message: 'Missing fields' }), {
        status: 400,
      });
    }

    const existingUser = await prisma.user.findUnique({ where: { email } });
    if (existingUser) {
      return new Response(JSON.stringify({ message: 'User already exists' }), {
        status: 409,
      });
    }

    const hashedPassword = await hash(password, 10);

    if (companyId) {
      await prisma.user.create({
        data: {
          email,
          password: hashedPassword,
          name,
          role: role,
          company: { connect: { id: companyId } },
        },
      });
    } else {
      await prisma.user.create({
        data: {
          email,
          password: hashedPassword,
          name,
          role: role,
        },
      });
    }

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error creating user:', err);
    return new Response(JSON.stringify({ message: 'Server error' }), {
      status: 500,
    });
  }
}

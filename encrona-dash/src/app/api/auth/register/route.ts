import { hash } from 'bcryptjs';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { email, password }: { email: string; password: string } = body;

    if (!email || !password) {
      return new Response(JSON.stringify({ message: 'Missing fields' }), {
        status: 400,
      });
    }

    const existingUser = await prisma.user.findUnique({
      where: { email },
    });

    if (existingUser) {
      return new Response(JSON.stringify({ message: 'User already exists' }), {
        status: 409,
      });
    }

    const hashedPassword = await hash(password, 10);

    await prisma.user.create({
      data: { email, password: hashedPassword },
    });

    return new Response(JSON.stringify({ message: 'User created' }), {
      status: 201,
    });
  } catch (error) {
    console.error('Registration error:', error);
    return new Response(JSON.stringify({ message: 'Internal server error' }), {
      status: 500,
    });
  }
}

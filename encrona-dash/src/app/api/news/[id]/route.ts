import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export async function DELETE(
  req: Request,
  { params }: { params: { id: string } }
) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const id = parseInt(params.id);
    await prisma.newsEntry.delete({ where: { id } });

    return new Response(null, { status: 204 });
  } catch (error) {
    console.error('Error deleting news entry:', error);
    return new Response(JSON.stringify({ message: 'Server error' }), {
      status: 500,
    });
  }
}

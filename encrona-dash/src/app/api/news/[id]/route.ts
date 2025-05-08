import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export async function DELETE(
  request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }
  const { id } = await params;

  try {
    const newsId = parseInt(id);
    await prisma.newsEntry.delete({ where: { id:newsId } });

    return new Response(null, { status: 204 });
  } catch (error) {
    console.error('Error deleting news entry:', error);
    return new Response(JSON.stringify({ message: 'Server error' }), {
      status: 500,
    });
  }
}

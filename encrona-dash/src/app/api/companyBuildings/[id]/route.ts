import { auth, isAdmin } from '@/lib/auth';
import prisma from '../../../../../prisma';

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

  const buildings = await prisma.building.findMany({
    where: {
      //This retrives buildings which match with the specified company id
      companiesWithAccess: {
        some: {
          id: id,
        },
      },
    },
    select: {
      id: true,
      name: true,
      owner: true,
      installedAt: true,
      createdAt: true,
      companiesWithAccess: {
        select: {
          name: true,
          id: true,
        },
      },
    },
  });

  return new Response(JSON.stringify(buildings), { status: 200 });
}

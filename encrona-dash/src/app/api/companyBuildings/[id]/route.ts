import { auth, isAdmin } from '@/lib/auth';
import prisma from '../../../../../prisma';

export async function GET(
  request: Request,
  { params }: { params: { id: string } }
) {
  const session = await auth();

  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }
  await params;
  const id = parseInt(params.id);

  const buildings = await prisma.building.findMany({
    where: {
      //This retrives buildings which match with the specified company id
      companiesWithAccess: {
        some: {
          companyId: id,
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

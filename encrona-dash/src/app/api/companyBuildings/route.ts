import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

export async function GET() {
  const session = await auth();

  let buildings;

  if (isAdmin(session)) {
    buildings = await prisma.building.findMany({
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
  } else {
    let id: { companyId: number | null } | null;
    try {
      if (session?.user.id == undefined) {
        throw Error();
      }
      id = await prisma.user.findUnique({
        where: { id: Number.parseInt(session?.user.id) },
        select: { companyId: true },
      });
      if (id == null || id.companyId == null) {
        throw Error();
      }
    } catch (error) {
      console.log(error);
      return new Response(
        'You do not belong to any company, please contact Encrona Support',
        { status: 400 }
      );
    }

    buildings = await prisma.building.findMany({
      where: {
        //This retrives buildings which match with the specified company id
        companiesWithAccess: {
          some: {
            id: id.companyId,
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
  }
  return new Response(JSON.stringify(buildings), { status: 200 });
}

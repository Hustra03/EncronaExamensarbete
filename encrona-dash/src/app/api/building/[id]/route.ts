import { auth, isAdmin } from '@/lib/auth';
import { PrismaClient } from '@prisma/client';
import { NextRequest } from 'next/server';

const prisma = new PrismaClient();

export async function GET(
  req: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id } = await params;
  const session = await auth();

  let building;

  const buildingId = parseInt(id);
  if (isNaN(buildingId)) {
    return new Response('Invalid building ID', { status: 400 });
  }

  if (!isAdmin(session)) {
    try {
      if (session?.user.id == undefined) {
        throw Error();
      }
      const companyId = await prisma.user.findUnique({
        where: { id: Number.parseInt(session?.user.id) },
        select: { companyId: true },
      });
      if (companyId == null || companyId.companyId == null) {
        throw Error();
      }
      building = await prisma.building.findUnique({
        where: {
          id: buildingId,
          //This retrives buildings which match with the specified company id
          companiesWithAccess: {
            some: {
              id: companyId.companyId,
            },
          },
        },
        select: { name: true, installedAt: true },
      });

      if (!building) {
        return new Response('Unauthorized', { status: 401 });
      }
    } catch (error) {
      console.log(error);
      return new Response(
        'You do not belong to any company, please contact Encrona Support',
        { status: 400 }
      );
    }
  } else {
    building = await prisma.building.findUnique({
      where: { id: buildingId },
      select: { name: true, installedAt: true },
    });
  }

  if (!building) {
    return new Response('Building not found', { status: 404 });
  }

  const actual = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ACTUAL' },
    orderBy: { date: 'asc' },
  });

  const estimate = await prisma.buildingData.findMany({
    where: { buildingId, type: 'ESTIMATE' },
    orderBy: { date: 'asc' },
  });

  return new Response(
    JSON.stringify({
      building,
      actual,
      estimate,
    }),
    { status: 200 }
  );
}

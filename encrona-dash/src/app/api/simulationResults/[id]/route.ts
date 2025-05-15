import { auth, isAdmin } from '@/lib/auth';
import { BuildingDataType, Prisma, PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

/**
 * This is done to retrive a type which includes the relevant objects, not just their ids
 */
type BuildingSimulationWithInclude = Prisma.BuildingSimulationGetPayload<{
  include: {
    heatCurve: true;
    electricityCurve: true;
    waterCurve: true;
    HeatSourceEstimation: true;
    electricityEstimation: true;
    waterEstimation: true;
  };
}>;

interface EstimateInterface {
  buildingId: number;
  type: BuildingDataType;
  date: Date;

  totalEnergykWh: number;
  spaceHeatingkWh: number;
  waterHeatingkWh: number;
  electricitykWh: number;
  totalWaterM3: number;

  totalEnergyCost: number;
  spaceHeatingCost: number;
  waterHeatingCost: number;
  electricityCost: number;
}

/**
 * This GET endpoint is used to request that the generation of new estimations for a specific building is checked, and if needed performed
 * @param id is the request param, and is the building id for which
 * @returns
 */
export async function GET(
  request: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const session = await auth();

  const { id } = await params;

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
        select: {
          name: true,
          owner: true,
          installedAt: true,
        },
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
      select: {
        name: true,
        owner: true,
        installedAt: true,
      },
    });
  }

  if (!building) {
    return new Response('Building not found', { status: 404 });
  }

  const simulationResults = await prisma.buildingSimulation.findFirst({
    where: {
      buildingId: buildingId,
    },
    include: {
      heatCurve: true,
      electricityCurve: true,
      waterCurve: true,
      HeatSourceEstimation: true,
      electricityEstimation: true,
      waterEstimation: true,
    },
  });

  if (!simulationResults) {
    return new Response('Building has no simulation results', { status: 404 });
  }

  return checkAndPotentiallyCreateEstimates(
    buildingId,
    building,
    simulationResults
  );
}

//This transaction function is used to ensure only one set of new estimate is created
//https://www.prisma.io/docs/orm/prisma-client/queries/transactions#interactive-transactions
function checkAndPotentiallyCreateEstimates(
  buildingId: number,
  building: { name: string; owner: string; installedAt: Date },
  simulationResults: BuildingSimulationWithInclude
) {
  return prisma.$transaction(async tx => {
    //We retrive the simulation information for this building

    //We retrive the estimates which exist (so we can confirm how many to generate)
    const estimate = await prisma.buildingData.findMany({
      where: {
        buildingId,
        type: 'ESTIMATE',
      },
      orderBy: { date: 'asc' },
    });

    const datesToAdd: Date[] = [];
    let year = building.installedAt.getFullYear();
    let month = building.installedAt.getMonth();
    const dateToAdd: Date = new Date();
    dateToAdd.setUTCHours(0, 0, 0, 0); //This removes hour,minute,second and millisecond information from estimates
    const dateToReach: { year: number; month: number } = {
      year: dateToAdd.getUTCFullYear() + 1,
      month: dateToAdd.getUTCMonth(),
    };

    //We then, using the estimate array and the buildings installedAt date, define which months are currently lacking an estimate
    let arrayIndex: number = 0;
    let add = true;

    dateToAdd.setUTCFullYear(year, month, 1);

    while (year < dateToReach.year || month <= dateToReach.month) {
      //This checks, as long as there are more estimates and we did not just find a match,
      //if the current index estimate is older than the currently investigated date,
      // if so increment index, but if it is not and it is this year we set add to false (since this date already has an estimate)
      if (arrayIndex < estimate.length) {
        if (
          dateToAdd.getFullYear() >= estimate[arrayIndex].date.getFullYear() &&
          dateToAdd.getMonth() > estimate[arrayIndex].date.getMonth()
        ) {
          arrayIndex += 1;
        } else {
          if (
            dateToAdd.getFullYear() ==
              estimate[arrayIndex].date.getFullYear() &&
            dateToAdd.getMonth() == estimate[arrayIndex].date.getMonth()
          ) {
            arrayIndex += 1;
            add = false;
          }
        }
      }
      if (add) {
        datesToAdd.push(new Date(dateToAdd.toLocaleDateString()));
      }
      add = true;
      month += 1;
      if (month == 12) {
        year += 1;
        month = 0;
        dateToAdd.setUTCFullYear(year, month);
      } else {
        dateToAdd.setUTCMonth(month);
      }
    }

    console.log(datesToAdd);

    const buildingEstimation: EstimateInterface[] = [];

    let electricityIndex = 0; //This is used to ensure we are using the correct value
    let waterIndex = 0; // -||-

    //Since there are multiple heat sources, we construct an array of unique names,
    //then use this to construct an array of arrays,
    // with each one being for each of the heat sources in the same way as electricity and water (with an accompanying array of indexes)

    const uniqueHeatSourceNames: string[] = [];
    simulationResults.HeatSourceEstimation.forEach(heatSource => {
      if (!uniqueHeatSourceNames.includes(heatSource.name)) {
        uniqueHeatSourceNames.push(heatSource.name);
      }
    });

    const heatSourceIndexes: number[] = [];
    const heatSourceArrayOfArrays: (typeof simulationResults.HeatSourceEstimation)[] =
      [];
    for (let index = 0; index < uniqueHeatSourceNames.length; index++) {
      heatSourceIndexes.push(0);
      heatSourceArrayOfArrays.push(
        simulationResults.HeatSourceEstimation.filter(
          heatSource => heatSource.name == uniqueHeatSourceNames[index]
        )
      );
    }

    datesToAdd.forEach(dateToAddEstimateFor => {
      const yearsSinceInstallation =
        dateToAddEstimateFor.getFullYear() - building.installedAt.getFullYear();

      if (
        electricityIndex + 1 <
        simulationResults.electricityEstimation.length
      ) {
        //And then that the current entry is out of date (the last entry should have year : -1, and is for after the improvements stop having a noticable effect)
        if (
          simulationResults.electricityEstimation[electricityIndex].year <
            yearsSinceInstallation &&
          electricityIndex < simulationResults.electricityEstimation.length
        ) {
          electricityIndex += 1;
        }
      }

      //We first test that there are more water entries
      if (waterIndex + 1 < simulationResults.waterEstimation.length) {
        //And then that the current entry is out of date (the last entry should have year : -1, and is for after the improvements stop having a noticable effect)
        if (
          simulationResults.waterEstimation[waterIndex].year <
            yearsSinceInstallation &&
          waterIndex < simulationResults.waterEstimation.length
        ) {
          waterIndex += 1;
        }
      }

      //We then do something similar for the heat sources, but with the uniqueHeatingSourcesYears array instead and for each heat source
      heatSourceArrayOfArrays.forEach((element, localIndexHere) => {
        if (heatSourceIndexes[localIndexHere] + 1 < element.length) {
          //And then that the current entry is out of date (the last entry should have year : -1, and is for after the improvements stop having a noticable effect)
          if (
            element[localIndexHere].year < yearsSinceInstallation &&
            localIndexHere < element.length
          ) {
            heatSourceIndexes[localIndexHere] += 1;
          }
        }
      });

      //We then calculate the current consumption values

      const currentElectricityCurveValue =
        simulationResults.electricityCurve.curve[
          dateToAddEstimateFor.getMonth()
        ];
      const electricitykWh =
        simulationResults.electricityEstimation[
          electricityIndex
        ].consumption.toNumber() * currentElectricityCurveValue.toNumber();

      let waterHeatingkWh = 0;
      let spaceHeatingkWh = 0;

      for (
        let heatForIndex = 0;
        heatForIndex < heatSourceArrayOfArrays.length;
        heatForIndex++
      ) {
        const HeatSourceEstimate =
          heatSourceArrayOfArrays[heatForIndex][
            heatSourceIndexes[heatForIndex]
          ];

        //This ensures that we only retrive heat source entries for the currently relevant range
        waterHeatingkWh +=
          HeatSourceEstimate.waterHeatingConsumption.toNumber();
        spaceHeatingkWh +=
          HeatSourceEstimate.buildingHeatingConsumption.toNumber();
      }

      const currentHeatingCurveValue =
        simulationResults.heatCurve.curve[dateToAddEstimateFor.getMonth()];

      spaceHeatingkWh = spaceHeatingkWh * currentHeatingCurveValue.toNumber();


      const currentWaterCurveValue =
        simulationResults.waterCurve.curve[dateToAddEstimateFor.getMonth()];
      const totalWaterM3 =
        simulationResults.waterEstimation[waterIndex].consumption.toNumber() *
        currentWaterCurveValue.toNumber();

      waterHeatingkWh = waterHeatingkWh * currentWaterCurveValue.toNumber();
      
      const totalEnergykWh = waterHeatingkWh + spaceHeatingkWh + electricitykWh;

      //TODO add cost calculation here, based on above values, once cost is calculatable

      buildingEstimation.push({
        buildingId: buildingId,
        type: BuildingDataType.ESTIMATE,
        date: new Date(dateToAddEstimateFor.toISOString()),

        totalEnergykWh,
        spaceHeatingkWh,
        waterHeatingkWh,
        electricitykWh,
        totalWaterM3,

        //TODO add cost calculation, based on above values, once cost is calculatable (currently just set to 0 since it needs to have a value here to work correctly)
        totalEnergyCost: 0,
        spaceHeatingCost: 0,
        waterHeatingCost: 0,
        electricityCost: 0,
      });
    });

    //We finally store the newly created estimates and return 200 ok
    await tx.buildingData.createMany({
      data: buildingEstimation,
    });

    return new Response(
      JSON.stringify({
        newEstimates: buildingEstimation,
      }),
      { status: 200 }
    );
  });
}

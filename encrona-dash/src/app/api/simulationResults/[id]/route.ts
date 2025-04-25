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
  { params }: { params: { id: string } }
) {
  const session = await auth();

  //TODO Add check here so that a signed-in user can access this if they have access to this specific building
  if (!isAdmin(session)) {
    return new Response('Unauthorized', { status: 401 });
  }

  const buildingId = parseInt(await params.id);
  if (isNaN(buildingId)) {
    return new Response('Invalid building ID : ' + params.id, { status: 400 });
  }

  const building = await prisma.building.findUnique({
    where: { id: buildingId },
    select: {
      name: true,
      owner: true,
      installedAt: true,
    },
  });

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

  //We retrive the estimates which exist for the future (so we can confirm how many to generate)
  const estimate = await prisma.buildingData.findMany({
    where: {
      buildingId,
      type: 'ESTIMATE',
      date: { gt: new Date() }, //This retrives entries with date some time in the future
      updatedAt: { lt: new Date() }, //This retrives entries with updatedAt some time in the past
    },
    orderBy: { date: 'desc' },
  });

  if (estimate.length >= 6) {
    return new Response(null, { status: 204 });
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

    //Note that we re-retrive the estimates and check them, since we now have the transaction, since someone else could have created something since we retrived it
    // this follows the test-test and set architecture
    const estimate = await tx.buildingData.findMany({
      where: {
        buildingId,
        type: 'ESTIMATE',
        date: { gt: new Date() }, //This retrives entries with date some time in the future
        updatedAt: { lt: new Date() }, //This retrives entries with updatedAt some time in the past
      },
      orderBy: { date: 'desc' },
    });

    if (estimate.length >= 6) {
      return new Response(null, { status: 204 });
    }

    let latestDate;
    if (estimate.length <= 0) {
      latestDate = building.installedAt;
    } else {
      latestDate = estimate[0].date;
    }

    //TODO add checks for if the dates are continous?

    //This is the latest date for which there exists an estimate

    const buildingEstimation: EstimateInterface[] = [];

    let yearsSinceInstallation =
      latestDate.getFullYear() - building.installedAt.getFullYear();
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
    const heatSourceArrayOfArrays = [];
    for (let index = 0; index < uniqueHeatSourceNames.length; index++) {
      heatSourceIndexes.push(0);
      heatSourceArrayOfArrays.push(
        simulationResults.HeatSourceEstimation.filter(
          heatSource => heatSource.name == uniqueHeatSourceNames[index]
        )
      );
    }

    //We then create estimates until there are 6 futures ones (this can be modified)
    for (let index = 0; index < 6 - estimate.length; index++) {
      //This increments the month, but if it is December it wraps around
      if (latestDate.getMonth() != 11) {
        latestDate.setMonth(latestDate.getMonth() + 1);
      } else {
        latestDate.setFullYear(latestDate.getFullYear() + 1);
        yearsSinceInstallation += 1;

        //We confirm that the year ranges for electricity, heat and water are still correct, if not we handle them

        //We first test that there are more electricity entries

        if (
          electricityIndex + 1 <
          simulationResults.electricityEstimation.length
        ) {
          //And then that the current entry is out of date (the last entry should have year : -1, and is for after the improvements stop having a noticable effect)
          if (
            simulationResults.electricityEstimation[electricityIndex].year <
              yearsSinceInstallation ||
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
              yearsSinceInstallation ||
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
              element[localIndexHere].year < yearsSinceInstallation ||
              waterIndex < element.length
            ) {
              heatSourceIndexes[localIndexHere] += 1;
            }
          }
        });
      }

      //We then calculate the current consumption values

      const currentElectricityCurveValue =
        simulationResults.electricityCurve.curve[latestDate.getMonth()];
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
        //console.log(heatSourceArrayOfArrays[index][heatSourceIndexes[heatForIndex]])

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
        simulationResults.heatCurve.curve[latestDate.getMonth()];

      waterHeatingkWh = waterHeatingkWh * currentHeatingCurveValue.toNumber();
      spaceHeatingkWh = waterHeatingkWh * currentHeatingCurveValue.toNumber();

      const totalEnergykWh = waterHeatingkWh + spaceHeatingkWh + electricitykWh;

      const currentWaterCurveValue =
        simulationResults.waterCurve.curve[latestDate.getMonth()];
      const totalWaterM3 =
        simulationResults.waterEstimation[waterIndex].consumption.toNumber() *
        currentWaterCurveValue.toNumber();

      //TODO add cost calculation here, based on above values, once cost is calculatable

      buildingEstimation.push({
        buildingId: buildingId,
        type: BuildingDataType.ESTIMATE,
        date: new Date(latestDate.toISOString()),

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
    }

    //We finally store the newly created estimates and return 200 ok

    //TODO store the new estimate entries

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

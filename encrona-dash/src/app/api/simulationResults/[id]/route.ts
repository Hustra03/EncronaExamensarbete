import { auth, isAdmin } from '@/lib/auth';
import { BuildingDataType, PrismaClient } from '@prisma/client';
import { Decimal } from '@prisma/client/runtime/library';

const prisma = new PrismaClient();

/**
 * This GET endpoint is used to request the generation of new estimations for a specific building
 * @param id is the request param, and is the building id for which  
 * @returns 
 */

interface EstimateInterface {
    building:{
        connect: {
          id: number,
        },
      },
    type:BuildingDataType,
    date:Date,      
  
    totalEnergykWh:Decimal, 
    spaceHeatingkWh:Decimal,
    waterHeatingkWh:Decimal,
    electricitykWh:Decimal,
    totalWaterM3:Decimal,

    totalEnergyCost:Decimal,
    spaceHeatingCost:Decimal,
    waterHeatingCost:Decimal,
    electricityCost:Decimal
  }

export async function GET(
    request: Request,
    { params }: { params: { id: string } }
  ) {
  const session = await auth();

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

  //We retrive the simulation information for this building
  const simulationResults = await prisma.buildingSimulation.findFirst({
    where:{
        buildingId:buildingId
    },
    include: {
        heatCurve: true,
        electricityCurve: true,
        waterCurve: true,
        HeatSourceEstimation: true,
        electricityEstimation: true,
        waterEstimation: true,
      },
  })

  console.log(simulationResults);

  if (!simulationResults) {
    return new Response('Building has no simulation results', { status: 404 });
  }

  //We retrive the estimates which exist for the future (so we can confirm how many to generate)
  const estimate = await prisma.buildingData.findMany({
    where: {
      buildingId,
      type: 'ESTIMATE',
      date: {gt:new Date()}, //This retrives entries with date some time in the future
      updatedAt: {lt:new Date()} //This retrives entries with updatedAt some time in the past
    },
    orderBy: { date: 'desc' },
  });

  if (estimate.length>6) {
    return new Response('There are already more than 6 future estimates for this building', { status: 404 });
  }

  //TODO add checks for if the dates are continous?

  //This is the latest date for which there exists an estimate
  const latestDate  = estimate[0].date;
  
  const buildingEstimation: EstimateInterface[] = [];

  let yearsSinceInstallation=latestDate.getFullYear()-building.installedAt.getFullYear();

  //We then create estimates until there are 6 futures ones (this can be modified)
  for (let index = 0; index < 6-estimate.length; index++) {

      //This increments the month, but if it is December it wraps around 
  if(latestDate.getMonth()!=11)
    {  latestDate.setMonth(latestDate.getMonth()+1);}
  else
  {
    latestDate.setMonth(0);
    latestDate.setFullYear(latestDate.getFullYear()+1);
    yearsSinceInstallation+=1;
  }

  //We then calculate the current consumption values
  
  const currentElectricityCurveValue=simulationResults.electricityCurve.curve[latestDate.getMonth()];
  const electricitykWh=(Decimal.mul(simulationResults.electricityEstimation[0].consumption,currentElectricityCurveValue));

  let waterHeatingkWh=simulationResults.HeatSourceEstimation[0].waterHeatingConsumption;
  let spaceHeatingkWh=simulationResults.HeatSourceEstimation[0].buildingHeatingConsumption;
  for (let index = 1; index < simulationResults.HeatSourceEstimation.length; index++) {
    const HeatSourceEstimate = simulationResults.HeatSourceEstimation[index];
    waterHeatingkWh=Decimal.add(waterHeatingkWh,HeatSourceEstimate.waterHeatingConsumption);
    spaceHeatingkWh=Decimal.add(spaceHeatingkWh,HeatSourceEstimate.buildingHeatingConsumption);
  }

  const currentHeatingCurveValue=simulationResults.heatCurve.curve[latestDate.getMonth()];

  waterHeatingkWh=Decimal.mul(waterHeatingkWh,currentHeatingCurveValue);
  spaceHeatingkWh=Decimal.mul(waterHeatingkWh,currentHeatingCurveValue);

  const totalEnergykWh=Decimal.add(Decimal.add(waterHeatingkWh,spaceHeatingkWh),electricitykWh);

  const currentWaterCurveValue=simulationResults.waterCurve.curve[latestDate.getMonth()];
  const totalWaterM3=Decimal.mul(simulationResults.waterEstimation[0].consumption,currentWaterCurveValue);

//TODO add cost calculation here, based on above values, once cost is calculatable

    buildingEstimation.push({
        building:{
            connect: {
              id: buildingId,
            },
          },
        type:BuildingDataType.ESTIMATE,
        date:new Date(latestDate.getDate()),      
      
        totalEnergykWh, 
        spaceHeatingkWh,
        waterHeatingkWh,
        electricitykWh,
        totalWaterM3,
    
        //TODO add cost calculation, based on above values, once cost is calculatable
        totalEnergyCost:new Decimal(0.0),
        spaceHeatingCost:new Decimal(0.0),
        waterHeatingCost:new Decimal(0.0),
        electricityCost:new Decimal(0.0)

    })
  }

  //We finally store the newly created estimates and return 200 ok


  return new Response(
    JSON.stringify({
      ...buildingEstimation,
      estimate,
    }),
    { status: 200 }
  );
}

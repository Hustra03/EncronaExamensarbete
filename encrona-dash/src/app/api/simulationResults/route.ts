import { BuildingDataType, PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

interface Estimation {
  year: number;
  consumption: number;
  savings: number;
}

/**
 * This function is used to submit the simulation results
 * @param request The API request, which is inspected to confirm that the user has access (using auth()) and to retrive the provided data
 * @returns A response, with status code 204 if the request was accepted, or 40* if something went wrong (if so a short error message is included)
 */
export async function POST(request: Request) {
  const session = await auth();

  //TODO if an owner is allowed to add their own simulation, add a check here that they own this building, as an alternative to being an admin
  if (!session || !isAdmin(session)) {
    return new Response(JSON.stringify({ message: 'Unauthorized' }), {
      status: 401,
    });
  }

  const body = await request.json();

  const { id, simulationResults } = body;

  if (!id || !simulationResults) {
    return new Response(
      JSON.stringify({ message: 'Missing required information' }),
      { status: 400 }
    );
  }

  let buildingId: number;
  try {
    buildingId = Number.parseInt(id);
  } catch {
    return new Response(JSON.stringify({ message: 'Building id is invalid' }), {
      status: 400,
    });
  }

  //We first try parsin the simulation results as a json, to confirm it is in the correct format
  let parsedSimulationResults;
  try {
    parsedSimulationResults = JSON.parse(simulationResults);
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Provided results is not a valid JSON' }),
      { status: 400 }
    );
  }

  //Here we could add some additional parsing of the json, to ensure the formatting is correct (This could be skipped, but would require changes to the later component creation)

  //We first test the 3 curves, that they exist and that they are the correct length (12)
  let heatCurve;
  try {
    heatCurve = parsedSimulationResults.heatingCurve;
    if (heatCurve.length != 12) {
      return new Response(
        JSON.stringify({ message: 'heatingCurve does not have 12 elements' }),
        { status: 400 }
      );
    }

    try {
      //TODO decide on if the curves must equal 1 exactly, current rounding does not always result in this even if the divided portions are equal to 1
      /*let count = 0;
      for (let index = 0; index < 12; index++) {
        count += heatingCurve[index];
        
      }
      if(count!=1.0)
      {        
        return new Response(
          JSON.stringify({ message: 'heatingCurve values does not add upp to 1, which it should' }),
          { status: 400 }
        );
      }*/
    } catch (error) {
      console.log(error);
      return new Response(
        JSON.stringify({
          message: 'heatingCurve does not contain parsable numbers',
        }),
        { status: 400 }
      );
    }
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'heatingCurve missing or invalid' }),
      { status: 400 }
    );
  }

  let waterCurve;
  try {
    waterCurve = parsedSimulationResults.waterCurve;
    if (waterCurve.length != 12) {
      return new Response(
        JSON.stringify({ message: 'waterCurve does not have 12 elements' }),
        { status: 400 }
      );
    }
    try {
      //TODO decide on if the curves must equal 1 exactly, current rounding does not always result in this even if the divided portions are equal to 1
      /*let count = 0;
      for (let index = 0; index < 12; index++) {
        count += waterCurve[index];
        
      }
      if(count!=1.0)
      {        
        return new Response(
          JSON.stringify({ message: 'waterCurve values does not add upp to 1, which it should' }),
          { status: 400 }
        );
      }*/
    } catch (error) {
      console.log(error);
      return new Response(
        JSON.stringify({
          message: 'waterCurve does not contain parsable numbers',
        }),
        { status: 400 }
      );
    }
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'waterCurve missing or invalid' }),
      { status: 400 }
    );
  }

  let electricityCurve;
  try {
    electricityCurve = parsedSimulationResults.electricityCurve;
    if (electricityCurve.length != 12) {
      return new Response(
        JSON.stringify({
          message: 'electricityCurve does not have 12 elements',
        }),
        { status: 400 }
      );
    }
    try {
      //TODO decide on if the curves must equal 1 exactly, current rounding does not always result in this even if the divided portions are equal to 1
      /*let count = 0;
      for (let index = 0; index < 12; index++) {
        count += electricityCurve[index];
        
      }
      if(count!=1.0)
      {        
        return new Response(
          JSON.stringify({ message: 'electricityCurve values does not add upp to 1, which it should' }),
          { status: 400 }
        );
      }*/
    } catch (error) {
      console.log(error);
      return new Response(
        JSON.stringify({
          message: 'electricityCurve does not contain parsable numbers',
        }),
        { status: 400 }
      );
    }
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'electricityCurve missing or invalid' }),
      { status: 400 }
    );
  }

  //We the parse the building id this simulation has to do with
  try {
    const rawId = JSON.parse(id);
    try {
      Number.parseInt(rawId);
    } catch (error) {
      console.log(error);
      return new Response(
        JSON.stringify({
          message: 'Building id provided is not a valid number',
        }),
        { status: 400 }
      );
    }
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({
        message: 'Unable to parse building id from provided building',
      }),
      { status: 400 }
    );
  }

  //We then parse electricity and water consumption + savings
  let parsedElectricitySavings;
  try {
    parsedElectricitySavings = parsedSimulationResults.electricitySavings;
    parsedElectricitySavings.forEach(
      (element: { year: string; electricityValue: string }) => {
        try {
          Number.parseInt(element.year);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in electricitySavings has an invalid year attribute',
            }),
            { status: 400 }
          );
        }

        try {
          Number.parseInt(element.electricityValue);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in electricitySavings has an invalid electricityValue attribute',
            }),
            { status: 400 }
          );
        }
      }
    );
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Unable to parse ElectricitySavings' }),
      { status: 400 }
    );
  }

  let parsedElectricityConsumption;
  try {
    parsedElectricityConsumption =
      parsedSimulationResults.electricityConsumption;

    parsedElectricityConsumption.forEach(
      (element: { year: string; electricityValue: string }) => {
        try {
          Number.parseInt(element.year);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in electricityConsumption has an invalid year attribute',
            }),
            { status: 400 }
          );
        }

        try {
          Number.parseInt(element.electricityValue);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in electricityConsumption has an invalid electricityValue attribute',
            }),
            { status: 400 }
          );
        }
      }
    );
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Unable to parse electricityConsumption' }),
      { status: 400 }
    );
  }

  let parsedWaterSavings;
  try {
    parsedWaterSavings = parsedSimulationResults.waterSavings;

    parsedWaterSavings.forEach(
      (element: { year: string; waterValue: string }) => {
        try {
          Number.parseInt(element.year);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message: 'Element in waterSavings has an invalid year attribute',
            }),
            { status: 400 }
          );
        }

        try {
          Number.parseInt(element.waterValue);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in waterSavings has an invalid electricityValue attribute',
            }),
            { status: 400 }
          );
        }
      }
    );
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Unable to parse waterSavings' }),
      { status: 400 }
    );
  }

  let parsedWaterConsumption;
  try {
    parsedWaterConsumption = parsedSimulationResults.waterConsumption;

    parsedWaterConsumption.forEach(
      (element: { year: string; waterValue: string }) => {
        try {
          Number.parseInt(element.year);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in waterConsumption has an invalid year attribute',
            }),
            { status: 400 }
          );
        }

        try {
          Number.parseInt(element.waterValue);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message:
                'Element in waterConsumption has an invalid electricityValue attribute',
            }),
            { status: 400 }
          );
        }
      }
    );
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Unable to parse waterConsumption' }),
      { status: 400 }
    );
  }

  //Then we parse the heat estimation

  let parsedHeatSources;
  try {
    parsedHeatSources = parsedSimulationResults.heatSources;

    parsedHeatSources.forEach(
      (element: {
        year: string;
        heatSource: {
          name: string;
          buildingHeatingConsumption: number;
          waterHeatingConsumption: number;
          buildingHeatingSavings: number;
          waterHeatingSavings: number;
        };
      }) => {
        try {
          Number.parseInt(element.year);
        } catch (error) {
          console.log(error);
          return new Response(
            JSON.stringify({
              message: 'Element in heatSources has an invalid year attribute',
            }),
            { status: 400 }
          );
        }
      }
    );
  } catch (error) {
    console.log(error);
    return new Response(
      JSON.stringify({ message: 'Unable to parse heatSources' }),
      { status: 400 }
    );
  }

  const electricityEstimation: Estimation[] = [];

  parsedElectricityConsumption.forEach(
    (consumption: { year: number; electricityValue: number }) => {
      parsedElectricitySavings.forEach(
        (saving: { year: number; electricityValue: number }) => {
          if (saving.year == consumption.year) {
            const Estimation = {
              year: consumption.year,
              consumption: consumption.electricityValue,
              savings: saving.electricityValue,
            };
            electricityEstimation.push(Estimation);
          }
        }
      );
    }
  );

  const waterEstimation: Estimation[] = [];

  parsedWaterConsumption.forEach(
    (consumption: { year: number; waterValue: number }) => {
      parsedWaterSavings.forEach(
        (saving: { year: number; waterValue: number }) => {
          if (saving.year == consumption.year) {
            waterEstimation.push({
              year: consumption.year,
              consumption: consumption.waterValue,
              savings: saving.waterValue,
            });
          }
        }
      );
    }
  );

  //Here we then store the newly created building simulation
  try {
    //We first create the estimations, since these consist of many different records, which should be created individually

    const electricityEstimationItem =
      await prisma.consumptionEstimation.createManyAndReturn({
        data: electricityEstimation,
      });

      console.log(electricityEstimationItem);

    const waterEstimationItem =
      await prisma.consumptionEstimation.createManyAndReturn({
        data: waterEstimation,
      });

    const heatSourceEstimationItem =
      await prisma.heatSourceEstimation.createManyAndReturn({
        data: parsedHeatSources,
      });

    /** 
   await prisma.heatSourceEstimation.create({
    data:{
      year: parsedHeatSources[0].heatSource[0].year,
      name:parsedHeatSources[0].heatSource[0].name,
      buildingHeatingConsumption:parsedHeatSources[0].heatSource[0].buildingHeatingConsumption,
      buildingHeatingSavings :parsedHeatSources[0].heatSource[0].buildingHeatingSavings,
      waterHeatingConsumption :parsedHeatSources[0].heatSource[0].waterHeatingConsumption,
      waterHeatingSavings :parsedHeatSources[0].heatSource[0].waterHeatingSavings,
    }
   })*/


  const previousValue = await prisma.buildingSimulation.findFirst({
    where: { buildingId },
  });
  if (previousValue) {
    //We update the simulation object, since a previous one already existed

    await prisma.buildingSimulation.update({
          where:{
          buildingId:buildingId
          },
      data: {
        heatCurve: {
          create: {
            curve: heatCurve,
          },
        },
        waterCurve: {
          create: {
            curve: waterCurve,
          },
        },
        electricityCurve: {
          create: {
            curve: electricityCurve,
          },
        },
        electricityEstimation: {
          set: [], //This removes any existing relations
          connect: electricityEstimationItem.map(estimation => ({
            id: estimation.id,
          })), // https://github.com/prisma/prisma/discussions/4709
        },
        waterEstimation: {
          set: [], //This removes any existing relations
          connect: waterEstimationItem.map(estimation => ({
            id: estimation.id,
          })),
        },
        HeatSourceEstimation: {
          set: [], //This removes any existing relations
          connect: heatSourceEstimationItem.map(estimation => ({
            id: estimation.id,
          })),
        },
      },
    });
    //We then remove any existing estimates for the future, so that they can be re-generated using the new simulation

   const {count}= await prisma.buildingData.deleteMany({
        where: {
        type:BuildingDataType.ESTIMATE,
        date:{gte: new Date().toISOString()}
        },
  })
  //console.log("Deleted "+count);
      return new Response(null, { status: 204 });

  }
  else
  {
//We then create the simulation object since it did not exist
    await prisma.buildingSimulation.create({
      data: {
        building: {
          connect: {
            id: buildingId,
          },
        },
        heatCurve: {
          create: {
            curve: heatCurve,
          },
        },
        waterCurve: {
          create: {
            curve: waterCurve,
          },
        },
        electricityCurve: {
          create: {
            curve: electricityCurve,
          },
        },
        electricityEstimation: {
          connect: electricityEstimationItem.map(estimation => ({
            id: estimation.id,
          })), // https://github.com/prisma/prisma/discussions/4709
        },
        waterEstimation: {
          connect: waterEstimationItem.map(estimation => ({
            id: estimation.id,
          })),
        },
        HeatSourceEstimation: {
          connect: heatSourceEstimationItem.map(estimation => ({
            id: estimation.id,
          })),
        },
      },
    });
  }

  
    return new Response(null, { status: 201 });
  } catch (err) {
    console.error('Error storing simulation:', err);
    return new Response(
      JSON.stringify({ message: 'simulation not stored due to an error' }),
      { status: 400 }
    );
  }
}

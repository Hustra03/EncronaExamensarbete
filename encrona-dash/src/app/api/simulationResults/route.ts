import { PrismaClient } from '@prisma/client';
import { auth, isAdmin } from '@/lib/auth';

const prisma = new PrismaClient();

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

  //Here we could add some additional parsing of the json, to ensure the formatting is correct (This could be skipped, since it is also done by prisma, but it is good to confirm this manually so that correct error messages can be given)

  //We first test the 3 curves, that they exist and that they are the correct length (12)
  let heatingCurve;
  try {
    heatingCurve = parsedSimulationResults.heatingCurve;
    if (heatingCurve.length != 12) {
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
    electricityCurve = parsedSimulationResults.waterCurve;
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

  //Here we then store the newly created building simulation
  try {

    //We first create the electricityEstimation, waterEstimation, HeatSourceEstimation 

    const electricityEstimation = await prisma.ConsumptionEstimation.createManyAndReturn({
      data: [
        { name: 'Alice', email: 'alice@prisma.io' },
        { name: 'Bob', email: 'bob@prisma.io' },
      ],
    })

    const waterEstimation = await prisma.ConsumptionEstimation.createManyAndReturn({
      data: [
        { name: 'Alice', email: 'alice@prisma.io' },
        { name: 'Bob', email: 'bob@prisma.io' },
      ],
    })

    const HeatSourceEstimation = await prisma.HeatSourceEstimation.createManyAndReturn({
      data: [
        { name: 'Alice', email: 'alice@prisma.io' },
        { name: 'Bob', email: 'bob@prisma.io' },
      ],
    })

    await prisma.buildingSimulation.create({
      data: {
        building:{
          connect: {
            id: parsedSimulationResults.id,
          },
        },
        heatingCurve,
        waterCurve,
        electricityCurve,
        electricityEstimation,
        waterEstimation,
        HeatSourceEstimation,
      },
    });

    return new Response(null, { status: 204 });
  } catch (err) {
    console.error('Error storing simulation:', err);
    return new Response(
      JSON.stringify({ message: 'simulation not stored due to an error' }),
      { status: 400 }
    );
  }
}

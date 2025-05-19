import { PrismaClient, PriceType } from '@prisma/client';

const prisma = new PrismaClient();

export async function recalculateBuildingCosts(
  buildingId: number,
  type: PriceType
): Promise<void> {
  const now = new Date();
  const firstOfCurrentMonth = new Date(
    Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), 1)
  );

  const forecasts = await prisma.priceForecast.findMany({
    where: { buildingId, type },
    orderBy: { dateFrom: 'asc' },
  });

  const buildingData = await prisma.buildingData.findMany({
    where: {
      buildingId,
      type: 'ESTIMATE',
      date: { gte: firstOfCurrentMonth },
    },
    orderBy: { date: 'asc' },
  });

  if (buildingData.length === 0) {
    console.log(`No estimate data to update for building ${buildingId}`);
    return;
  }

  const updates = [];
  let forecastIndex = 0;

  for (const entry of buildingData) {
    const entryDate = entry.date;

    while (
      forecastIndex + 1 < forecasts.length &&
      forecasts[forecastIndex + 1].dateFrom <= entryDate
    ) {
      forecastIndex++;
    }

    const matchingForecast =
      forecasts[forecastIndex]?.dateFrom <= entryDate
        ? forecasts[forecastIndex]
        : undefined;

    const currentPrice = matchingForecast?.price?.toNumber() ?? 0;

    const updateData: Partial<typeof entry> = {};

    switch (type) {
      case 'HEATING':
        updateData.spaceHeatingCost = entry.spaceHeatingkWh * currentPrice;
        updateData.totalEnergyCost =
          updateData.spaceHeatingCost +
          entry.waterHeatingCost +
          entry.electricityCost;
        break;

      case 'WATERHEATING':
        updateData.waterHeatingCost = entry.waterHeatingkWh * currentPrice;
        updateData.totalEnergyCost =
          entry.spaceHeatingCost +
          updateData.waterHeatingCost +
          entry.electricityCost;
        break;

      case 'ELECTRICITY':
        updateData.electricityCost = entry.electricitykWh * currentPrice;
        updateData.totalEnergyCost =
          entry.spaceHeatingCost +
          entry.waterHeatingCost +
          updateData.electricityCost;
        break;

      case 'WATERUSAGE':
        updateData.totalWaterCost = entry.totalWaterM3 * currentPrice;
        break;
    }

    updates.push(
      prisma.buildingData.update({
        where: { id: entry.id },
        data: updateData,
      })
    );
  }

  if (updates.length > 0) {
    await prisma.$transaction(updates);
    console.log(
      `Updated ${updates.length} rows for ${type} in building ${buildingId}`
    );
  } else {
    console.log(
      `No applicable forecasted updates for building ${buildingId}, type ${type}`
    );
  }
}

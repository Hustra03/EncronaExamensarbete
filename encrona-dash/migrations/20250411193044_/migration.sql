/*
  Warnings:

  - You are about to drop the column `owner` on the `Building` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "BuildingData" DROP CONSTRAINT "BuildingData_buildingId_fkey";

-- AlterTable
ALTER TABLE "Building" DROP COLUMN "owner";

-- AlterTable
ALTER TABLE "BuildingData" ALTER COLUMN "totalEnergykWh" DROP NOT NULL,
ALTER COLUMN "spaceHeatingkWh" DROP NOT NULL,
ALTER COLUMN "waterHeatingkWh" DROP NOT NULL,
ALTER COLUMN "electricitykWh" DROP NOT NULL,
ALTER COLUMN "totalWaterM3" DROP NOT NULL,
ALTER COLUMN "totalEnergyCost" DROP NOT NULL,
ALTER COLUMN "spaceHeatingCost" DROP NOT NULL,
ALTER COLUMN "waterHeatingCost" DROP NOT NULL,
ALTER COLUMN "electricityCost" DROP NOT NULL;

-- AddForeignKey
ALTER TABLE "BuildingData" ADD CONSTRAINT "BuildingData_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE CASCADE ON UPDATE CASCADE;

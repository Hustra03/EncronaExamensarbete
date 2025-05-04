/*
  Warnings:

  - Made the column `totalEnergykWh` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `spaceHeatingkWh` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `waterHeatingkWh` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `electricitykWh` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `totalWaterM3` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `totalEnergyCost` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `spaceHeatingCost` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `waterHeatingCost` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `electricityCost` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.
  - Made the column `totalWaterCost` on table `BuildingData` required. This step will fail if there are existing NULL values in that column.

*/
-- AlterTable
ALTER TABLE "BuildingData" ALTER COLUMN "totalEnergykWh" SET NOT NULL,
ALTER COLUMN "totalEnergykWh" SET DEFAULT 0,
ALTER COLUMN "spaceHeatingkWh" SET NOT NULL,
ALTER COLUMN "spaceHeatingkWh" SET DEFAULT 0,
ALTER COLUMN "waterHeatingkWh" SET NOT NULL,
ALTER COLUMN "waterHeatingkWh" SET DEFAULT 0,
ALTER COLUMN "electricitykWh" SET NOT NULL,
ALTER COLUMN "electricitykWh" SET DEFAULT 0,
ALTER COLUMN "totalWaterM3" SET NOT NULL,
ALTER COLUMN "totalWaterM3" SET DEFAULT 0,
ALTER COLUMN "totalEnergyCost" SET NOT NULL,
ALTER COLUMN "totalEnergyCost" SET DEFAULT 0,
ALTER COLUMN "spaceHeatingCost" SET NOT NULL,
ALTER COLUMN "spaceHeatingCost" SET DEFAULT 0,
ALTER COLUMN "waterHeatingCost" SET NOT NULL,
ALTER COLUMN "waterHeatingCost" SET DEFAULT 0,
ALTER COLUMN "electricityCost" SET NOT NULL,
ALTER COLUMN "electricityCost" SET DEFAULT 0,
ALTER COLUMN "totalWaterCost" SET NOT NULL,
ALTER COLUMN "totalWaterCost" SET DEFAULT 0;

-- CreateEnum
CREATE TYPE "BuildingDataType" AS ENUM ('ESTIMATE', 'ACTUAL');

-- AlterTable
ALTER TABLE "Building" ALTER COLUMN "installedAt" DROP DEFAULT;

-- CreateTable
CREATE TABLE "BuildingData" (
    "id" SERIAL NOT NULL,
    "buildingId" INTEGER NOT NULL,
    "type" "BuildingDataType" NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "totalEnergykWh" DOUBLE PRECISION NOT NULL,
    "spaceHeatingkWh" DOUBLE PRECISION NOT NULL,
    "waterHeatingkWh" DOUBLE PRECISION NOT NULL,
    "electricitykWh" DOUBLE PRECISION NOT NULL,
    "totalWaterM3" DOUBLE PRECISION NOT NULL,
    "totalEnergyCost" DOUBLE PRECISION NOT NULL,
    "spaceHeatingCost" DOUBLE PRECISION NOT NULL,
    "waterHeatingCost" DOUBLE PRECISION NOT NULL,
    "electricityCost" DOUBLE PRECISION NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "BuildingData_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "BuildingData_buildingId_type_date_key" ON "BuildingData"("buildingId", "type", "date");

-- AddForeignKey
ALTER TABLE "BuildingData" ADD CONSTRAINT "BuildingData_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

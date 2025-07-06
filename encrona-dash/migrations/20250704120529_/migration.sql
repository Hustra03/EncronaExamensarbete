/*
  Warnings:

  - A unique constraint covering the columns `[buildingId,type,energyType]` on the table `Benchmark` will be added. If there are existing duplicate values, this will fail.
  - Added the required column `energyType` to the `Benchmark` table without a default value. This is not possible if the table is not empty.

*/
-- CreateEnum
CREATE TYPE "EnergyCategory" AS ENUM ('TOTAL', 'SPACE_HEATING', 'WATER_HEATING', 'ELECTRICITY', 'WATER_USAGE');

-- DropForeignKey
ALTER TABLE "Benchmark" DROP CONSTRAINT "Benchmark_buildingId_fkey";

-- AlterTable
ALTER TABLE "Benchmark" ADD COLUMN     "energyType" "EnergyCategory" NOT NULL,
ADD COLUMN     "unit" TEXT;

-- CreateIndex
CREATE UNIQUE INDEX "Benchmark_buildingId_type_energyType_key" ON "Benchmark"("buildingId", "type", "energyType");

-- AddForeignKey
ALTER TABLE "Benchmark" ADD CONSTRAINT "Benchmark_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE CASCADE ON UPDATE CASCADE;

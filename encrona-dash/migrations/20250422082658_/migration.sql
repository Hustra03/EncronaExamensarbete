-- DropForeignKey
ALTER TABLE "HeatSourceEstimation" DROP CONSTRAINT "HeatSourceEstimation_simulationId_fkey";

-- AlterTable
ALTER TABLE "HeatSourceEstimation" ALTER COLUMN "simulationId" DROP NOT NULL;

-- AddForeignKey
ALTER TABLE "HeatSourceEstimation" ADD CONSTRAINT "HeatSourceEstimation_simulationId_fkey" FOREIGN KEY ("simulationId") REFERENCES "BuildingSimulation"("id") ON DELETE SET NULL ON UPDATE CASCADE;

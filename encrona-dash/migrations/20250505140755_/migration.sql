-- DropForeignKey
ALTER TABLE "BuildingSimulation" DROP CONSTRAINT "BuildingSimulation_buildingId_fkey";

-- AddForeignKey
ALTER TABLE "BuildingSimulation" ADD CONSTRAINT "BuildingSimulation_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE CASCADE ON UPDATE CASCADE;

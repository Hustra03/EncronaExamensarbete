/*
  Warnings:

  - You are about to alter the column `curve` on the `Curve` table. The data in that column could be lost. The data in that column will be cast from `Decimal(65,30)` to `Integer`.
  - You are about to drop the column `simulationId` on the `HeatSourceEstimation` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "HeatSourceEstimation" DROP CONSTRAINT "HeatSourceEstimation_simulationId_fkey";

-- AlterTable
ALTER TABLE "Curve" ALTER COLUMN "curve" SET DATA TYPE INTEGER[];

-- AlterTable
ALTER TABLE "HeatSourceEstimation" DROP COLUMN "simulationId";

-- CreateTable
CREATE TABLE "_HeatSourceEstimation" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_HeatSourceEstimation_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateIndex
CREATE INDEX "_HeatSourceEstimation_B_index" ON "_HeatSourceEstimation"("B");

-- AddForeignKey
ALTER TABLE "_HeatSourceEstimation" ADD CONSTRAINT "_HeatSourceEstimation_A_fkey" FOREIGN KEY ("A") REFERENCES "BuildingSimulation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_HeatSourceEstimation" ADD CONSTRAINT "_HeatSourceEstimation_B_fkey" FOREIGN KEY ("B") REFERENCES "HeatSourceEstimation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

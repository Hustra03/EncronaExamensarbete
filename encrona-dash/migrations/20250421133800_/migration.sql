-- CreateTable
CREATE TABLE "Curve" (
    "id" SERIAL NOT NULL,
    "curve" INTEGER[],

    CONSTRAINT "Curve_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "ConsumptionEstimation" (
    "id" SERIAL NOT NULL,
    "year" INTEGER NOT NULL,
    "consumption" DECIMAL(65,30) NOT NULL,
    "savings" DECIMAL(65,30) NOT NULL,

    CONSTRAINT "ConsumptionEstimation_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "HeatSourceEstimation" (
    "id" SERIAL NOT NULL,
    "simulationId" INTEGER NOT NULL,
    "year" INTEGER NOT NULL,
    "name" TEXT NOT NULL,
    "buildingHeatingConsumption" DECIMAL(65,30) NOT NULL,
    "buildingHeatingSavings" DECIMAL(65,30) NOT NULL,
    "waterHeatingConsumption" DECIMAL(65,30) NOT NULL,
    "waterHeatingSavings" DECIMAL(65,30) NOT NULL,

    CONSTRAINT "HeatSourceEstimation_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "BuildingSimulation" (
    "id" SERIAL NOT NULL,
    "buildingId" INTEGER NOT NULL,
    "heatCurveId" INTEGER NOT NULL,
    "waterCurveId" INTEGER NOT NULL,
    "electricityId" INTEGER NOT NULL,

    CONSTRAINT "BuildingSimulation_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "_electricityEstimation" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_electricityEstimation_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateTable
CREATE TABLE "_waterEstimation" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,

    CONSTRAINT "_waterEstimation_AB_pkey" PRIMARY KEY ("A","B")
);

-- CreateIndex
CREATE UNIQUE INDEX "BuildingSimulation_buildingId_key" ON "BuildingSimulation"("buildingId");

-- CreateIndex
CREATE INDEX "_electricityEstimation_B_index" ON "_electricityEstimation"("B");

-- CreateIndex
CREATE INDEX "_waterEstimation_B_index" ON "_waterEstimation"("B");

-- AddForeignKey
ALTER TABLE "HeatSourceEstimation" ADD CONSTRAINT "HeatSourceEstimation_simulationId_fkey" FOREIGN KEY ("simulationId") REFERENCES "BuildingSimulation"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BuildingSimulation" ADD CONSTRAINT "BuildingSimulation_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BuildingSimulation" ADD CONSTRAINT "BuildingSimulation_heatCurveId_fkey" FOREIGN KEY ("heatCurveId") REFERENCES "Curve"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BuildingSimulation" ADD CONSTRAINT "BuildingSimulation_waterCurveId_fkey" FOREIGN KEY ("waterCurveId") REFERENCES "Curve"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BuildingSimulation" ADD CONSTRAINT "BuildingSimulation_electricityId_fkey" FOREIGN KEY ("electricityId") REFERENCES "Curve"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_electricityEstimation" ADD CONSTRAINT "_electricityEstimation_A_fkey" FOREIGN KEY ("A") REFERENCES "BuildingSimulation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_electricityEstimation" ADD CONSTRAINT "_electricityEstimation_B_fkey" FOREIGN KEY ("B") REFERENCES "ConsumptionEstimation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_waterEstimation" ADD CONSTRAINT "_waterEstimation_A_fkey" FOREIGN KEY ("A") REFERENCES "BuildingSimulation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_waterEstimation" ADD CONSTRAINT "_waterEstimation_B_fkey" FOREIGN KEY ("B") REFERENCES "ConsumptionEstimation"("id") ON DELETE CASCADE ON UPDATE CASCADE;

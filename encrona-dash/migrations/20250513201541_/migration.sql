-- CreateEnum
CREATE TYPE "PriceType" AS ENUM ('HEATING', 'ELECTRICITY', 'WATERHEATING', 'WATERUSAGE');

-- CreateTable
CREATE TABLE "PriceForecast" (
    "id" SERIAL NOT NULL,
    "buildingId" INTEGER NOT NULL,
    "dateFrom" TIMESTAMP(3) NOT NULL,
    "price" DECIMAL(65,30) NOT NULL,
    "type" "PriceType" NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "PriceForecast_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "PriceForecast_buildingId_dateFrom_type_idx" ON "PriceForecast"("buildingId", "dateFrom", "type");

-- CreateIndex
CREATE UNIQUE INDEX "PriceForecast_buildingId_dateFrom_type_key" ON "PriceForecast"("buildingId", "dateFrom", "type");

-- AddForeignKey
ALTER TABLE "PriceForecast" ADD CONSTRAINT "PriceForecast_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE CASCADE ON UPDATE CASCADE;

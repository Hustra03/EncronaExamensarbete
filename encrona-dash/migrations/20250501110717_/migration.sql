/*
  Warnings:

  - You are about to drop the column `mivoId` on the `Building` table. All the data in the column will be lost.

*/
-- CreateEnum
CREATE TYPE "DeviceSource" AS ENUM ('BELIMO', 'MIVO');

-- DropIndex
DROP INDEX "Building_mivoId_key";

-- AlterTable
ALTER TABLE "Building" DROP COLUMN "mivoId";

-- CreateTable
CREATE TABLE "Device" (
    "id" SERIAL NOT NULL,
    "buildingId" INTEGER NOT NULL,
    "source" "DeviceSource" NOT NULL,
    "externalId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Device_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Device_source_externalId_key" ON "Device"("source", "externalId");

-- AddForeignKey
ALTER TABLE "Device" ADD CONSTRAINT "Device_buildingId_fkey" FOREIGN KEY ("buildingId") REFERENCES "Building"("id") ON DELETE CASCADE ON UPDATE CASCADE;

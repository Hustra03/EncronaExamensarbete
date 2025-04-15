/*
  Warnings:

  - A unique constraint covering the columns `[mivoId]` on the table `Building` will be added. If there are existing duplicate values, this will fail.

*/
-- AlterTable
ALTER TABLE "Building" ADD COLUMN     "mivoId" TEXT;

-- CreateIndex
CREATE UNIQUE INDEX "Building_mivoId_key" ON "Building"("mivoId");

'use client';

import React, { useEffect, useState } from 'react';
import { useSession } from 'next-auth/react';
import {
  ColumnDef,
  ColumnFiltersState,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  useReactTable,
} from '@tanstack/react-table';

import { DataTableFilter } from '@/components/data-table/filter';
import { DataTablePagination } from '@/components/data-table/pagination';
import { DataTableCore } from '@/components/data-table/table';
import { BuildingSheet } from '@/components/building-sheet';
import type { Building } from './columns';

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  onRefresh: () => void;
}

export function DataTable<TData extends Building, TValue>({
  columns,
  data,
  onRefresh,
}: DataTableProps<TData, TValue>) {
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const { data: session } = useSession();

  const [editingBuilding, setEditingBuilding] = useState<Building | null>(null);
  const [editingBuildingOpen, setEditingBuildingOpen] = useState(false);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnFiltersChange: setColumnFilters,
    state: {
      columnFilters,
    },
  });

  // Used to listen for events emitted from the columns
  useEffect(() => {
    const handleEdit = (e: Event) => {
      const customEvent = e as CustomEvent<Building>;
      setEditingBuilding(customEvent.detail);
      setEditingBuildingOpen(true);
    };

    const handleRefresh = () => onRefresh();

    window.addEventListener('edit-building', handleEdit);
    window.addEventListener('refresh-buildings', handleRefresh);

    return () => {
      window.removeEventListener('edit-building', handleEdit);
      window.removeEventListener('refresh-buildings', handleRefresh);
    };
  }, [onRefresh]);

  // To clear the state when closing, otherwise the sheet will not be rerendered with correct data later on
  useEffect(() => {
    if (editingBuildingOpen === false) {
      const timeout = setTimeout(() => {
        setEditingBuilding(null);
      }, 200);

      return () => clearTimeout(timeout);
    }
  }, [editingBuildingOpen]);

  return (
    <div className="space-y-4">
      <div className="flex w-full items-center justify-between">
        <DataTableFilter table={table} />
        <BuildingSheet
          session={session}
          onSubmit={async data => {
            await fetch('/api/buildings', {
              method: 'POST',
              body: JSON.stringify(data),
              headers: { 'Content-Type': 'application/json' },
            });
            onRefresh();
          }}
        />
      </div>

      {editingBuilding && (
        <BuildingSheet
          hideTrigger
          session={session}
          open={editingBuildingOpen}
          onOpenChange={setEditingBuildingOpen}
          title="Redigera byggnad"
          description="Uppdatera informationen nedan."
          confirmLabel="Spara ändringar"
          defaultValues={editingBuilding}
          onSubmit={async formData => {
            await fetch(`/api/buildings/${editingBuilding.id}`, {
              method: 'PUT',
              body: JSON.stringify(formData),
              headers: { 'Content-Type': 'application/json' },
            });
            setEditingBuilding(null);
            onRefresh();
          }}
        />
      )}

      <DataTableCore table={table} columns={columns} />
      <DataTablePagination table={table} units={'Byggnader'} />
    </div>
  );
}

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
import { CompanySheet } from '@/components/company-sheet';
import type { Company } from './columns';
import { toast } from 'sonner';

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  onRefresh: () => void;
}

export function DataTable<TData extends Company, TValue>({
  columns,
  data,
  onRefresh,
}: DataTableProps<TData, TValue>) {
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const { data: session } = useSession();

  const [editingCompany, setEditingCompany] = useState<Company | null>(null);
  const [editingCompanyOpen, setEditingCompanyOpen] = useState(false);

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
      const customEvent = e as CustomEvent<Company>;
      setEditingCompany(customEvent.detail);
      setEditingCompanyOpen(true);
    };

    const handleRefresh = () => onRefresh();

    window.addEventListener('edit-company', handleEdit);
    window.addEventListener('refresh-company', handleRefresh);

    return () => {
      window.removeEventListener('edit-company', handleEdit);
      window.removeEventListener('refresh-company', handleRefresh);
    };
  }, [onRefresh]);

  // To clear the state when closing, otherwise the sheet will not be rerendered with correct data later on
  useEffect(() => {
    if (editingCompanyOpen === false) {
      const timeout = setTimeout(() => {
        setEditingCompany(null);
      }, 200);

      return () => clearTimeout(timeout);
    }
  }, [editingCompanyOpen]);

  return (
    <div className="space-y-4">
      <div className="flex w-full items-center justify-between">
        <DataTableFilter table={table} />
        <CompanySheet
          session={session}
          onSubmit={async data => {
            const res = await fetch('/api/company', {
              method: 'POST',
              body: JSON.stringify(data),
              headers: { 'Content-Type': 'application/json' },
            });
            if (res.status === 204) {
              toast('Företag skapat.');
              onRefresh();
            } else {
              toast('Något gick fel när företaget skulle skapas.');
            }
          }}
        />
      </div>

      {editingCompany && (
        <CompanySheet
          hideTrigger
          session={session}
          open={editingCompanyOpen}
          onOpenChange={setEditingCompanyOpen}
          title="Redigera företag"
          description="Uppdatera informationen nedan."
          confirmLabel="Spara ändringar"
          defaultValues={editingCompany}
          onSubmit={async formData => {
            const res = await fetch(`/api/company/${editingCompany.id}`, {
              method: 'PUT',
              body: JSON.stringify(formData),
              headers: { 'Content-Type': 'application/json' },
            });
            setEditingCompany(null);
            if (res.status === 204) {
              toast('Företag uppdaterat.');
              onRefresh();
            } else {
              toast('Något gick fel när företaget skulle uppdateras.');
            }
          }}
        />
      )}

      <DataTableCore table={table} columns={columns} />
      <DataTablePagination table={table} units={'Företag'} />
    </div>
  );
}

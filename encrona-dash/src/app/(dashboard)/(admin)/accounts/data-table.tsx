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
import { AccountSheet } from '@/components/account-sheet';
import type { User } from './columns';
import { Company } from '../company/columns';

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  companies: Company[];
  onRefresh: () => void;
}

export function DataTable<TData extends User, TValue>({
  columns,
  data,
  companies,
  onRefresh,
}: DataTableProps<TData, TValue>) {
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const { data: session } = useSession();

  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [editingUserOpen, setEditingUserOpen] = useState(false);

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
      const customEvent = e as CustomEvent<User>;
      setEditingUser(customEvent.detail);
      setEditingUserOpen(true);
    };

    const handleRefresh = () => onRefresh();

    window.addEventListener('edit-user', handleEdit);
    window.addEventListener('refresh-users', handleRefresh);

    return () => {
      window.removeEventListener('edit-user', handleEdit);
      window.removeEventListener('refresh-users', handleRefresh);
    };
  }, [onRefresh]);

  // To clear the state when closing, otherwise the sheet will not be rerendered with correct data later on
  useEffect(() => {
    if (editingUserOpen === false) {
      const timeout = setTimeout(() => {
        setEditingUser(null);
      }, 200);

      return () => clearTimeout(timeout);
    }
  }, [editingUserOpen]);

  return (
    <div className="space-y-4">
      <div className="flex w-full items-center justify-between">
        <DataTableFilter table={table} />
        <AccountSheet
          session={session}
          companies={companies}
          onSubmit={async data => {
            await fetch('/api/accounts', {
              method: 'POST',
              body: JSON.stringify(data),
              headers: { 'Content-Type': 'application/json' },
            });
            onRefresh();
          }}
        />
      </div>

      {editingUser && (
        <AccountSheet
          hideTrigger
          session={session}
          companies={companies}
          open={editingUserOpen}
          onOpenChange={setEditingUserOpen}
          title="Redigera användare"
          description="Uppdatera informationen nedan."
          confirmLabel="Spara ändringar"
          defaultValues={editingUser}
          onSubmit={async formData => {
            await fetch(`/api/accounts/${editingUser.id}`, {
              method: 'PUT',
              body: JSON.stringify(formData),
              headers: { 'Content-Type': 'application/json' },
            });
            setEditingUser(null);
            onRefresh();
          }}
        />
      )}

      <DataTableCore table={table} columns={columns} />
      <DataTablePagination table={table} units={'Användare'} />
    </div>
  );
}

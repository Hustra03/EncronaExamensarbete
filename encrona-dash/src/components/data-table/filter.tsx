'use client';

import { Input } from '@/components/ui/input';
import { Table } from '@tanstack/react-table';

interface DataTableFilterProps<TData> {
  table: Table<TData>;
}

export function DataTableFilter<TData>({ table }: DataTableFilterProps<TData>) {
  return (
    <div className="flex items-center py-4">
      <Input
        placeholder="Sök baserat på namn..."
        value={(table.getColumn('name')?.getFilterValue() as string) ?? ''}
        onChange={event => {
          const value = event.target.value;
          table.getColumn('name')?.setFilterValue(value);
        }}
        className="max-w-sm"
      />
    </div>
  );
}

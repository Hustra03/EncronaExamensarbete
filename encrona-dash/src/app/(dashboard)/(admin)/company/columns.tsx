'use client';

import { ColumnDef } from '@tanstack/react-table';
import { format } from 'date-fns';

import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction,
  AlertDialogDescription,
} from '@/components/ui/alert-dialog';
import { MoreVertical } from 'lucide-react';

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type Company = {
  id: string;
  name: string;
  owner: string;
};

export const columns: ColumnDef<Company>[] = [
  {
    accessorKey: 'name',
    header: 'Namn',
  },
  {
    accessorKey: 'owner',
    header: 'Ägare',
  },
  {
    accessorKey: 'createdAt',
    header: 'Registrerad',
    cell: ({ row }) => {
      const formatted = format(row.getValue('createdAt'), 'yyyy-MM-dd');
      return <div>{formatted}</div>;
    },
  },

  {
    id: 'actions',
    cell: ({ row }) => {
      const company = row.original;
      /* eslint-disable react-hooks/rules-of-hooks */
      const router = useRouter();
      return (
        <AlertDialog>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <MoreVertical />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem
                onClick={() => {
                  router.push(`/company/${company.id}`);
                }}
              >
                Visa
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => {
                  window.dispatchEvent(
                    new CustomEvent('edit-company', { detail: company })
                  );
                }}
              >
                Ändra
              </DropdownMenuItem>
              <AlertDialogTrigger asChild>
                <DropdownMenuItem>Ta bort</DropdownMenuItem>
              </AlertDialogTrigger>
            </DropdownMenuContent>
          </DropdownMenu>

          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Är du säker?</AlertDialogTitle>
              <AlertDialogDescription>
                Detta kommer att ta bort företaget{' '}
                <strong>{company.name}</strong>. Åtgärden går inte att ångra.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction
                onClick={async () => {
                  await fetch(`/api/company/${company.id}`, {
                    method: 'DELETE',
                  });
                  window.dispatchEvent(new Event('refresh-company'));
                }}
              >
                Ta bort
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      );
    },
  },
];

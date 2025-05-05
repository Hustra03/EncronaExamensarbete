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
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area';

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type Building = {
  id: string;
  name: string;
  owner: string;
  installedAt: string;
};

export const columns: ColumnDef<Building>[] = [
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
    accessorKey: 'installedAt',
    header: 'Installerad',
    cell: ({ row }) => {
      const formatted = format(row.getValue('installedAt'), 'yyyy-MM-dd');
      return <div>{formatted}</div>;
    },
  },
  {
    accessorKey: 'companiesWithAccess',
    header: 'Företag som har tillgång',
    cell: ({ row }) => {
      const formatted: { id: number; name: string }[] = row.getValue(
        'companiesWithAccess'
      );

      if (formatted.length > 0) {
        return (
          <ScrollArea className="rounded-md border whitespace-nowrap">
            {formatted.map(company => (
              <div key={company.id} className="mt-5 mb-5">
                <p className="text-center">{company.name}</p>
              </div>
            ))}
            <ScrollBar />
          </ScrollArea>
        );
      }
    },
  },

  {
    id: 'actions',
    cell: ({ row }) => {
      const building = row.original;
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
                  router.push(`/building/${building.id}`);
                }}
              >
                Visa
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => {
                  window.dispatchEvent(
                    new CustomEvent('edit-building', { detail: building })
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
                Detta kommer att ta bort byggnaden{' '}
                <strong>{building.name}</strong>. Åtgärden går inte att ångra.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction
                onClick={async () => {
                  await fetch(`/api/buildings/${building.id}`, {
                    method: 'DELETE',
                  });
                  window.dispatchEvent(new Event('refresh-buildings'));
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

'use client';

import { Role } from '@/lib/auth';
import { ColumnDef } from '@tanstack/react-table';
import { format } from 'date-fns';

import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
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
import { Company } from '../company/columns';
import { toast } from 'sonner';

// This type is used to define the shape of our data.
// You can use a Zod schema here if you want.
export type User = {
  id: string;
  name: string;
  email: string;
  role: Role;
  company: string;
  createdAt: Date;
};

export const columns: ColumnDef<User>[] = [
  {
    accessorKey: 'name',
    header: 'Namn',
  },
  {
    accessorKey: 'email',
    header: 'Email',
  },
  {
    accessorKey: 'role',
    header: 'Roll',
    cell: ({ row }) => {
      const role: Role = row.getValue('role');
      return (
        <Badge variant={role === 'ADMIN' ? 'destructive' : 'secondary'}>
          {role === 'ADMIN' ? 'Administratör' : 'Användare'}
        </Badge>
      );
    },
  },
  {
    accessorKey: 'company',
    header: 'Företag',
    cell: ({ row }) => {
      const formatted: Company = row.getValue('company');

      if (formatted) {
        return <div>{formatted.name}</div>;
      } else {
        return <div></div>;
      }
    },
  },
  {
    accessorKey: 'createdAt',
    header: 'Skapad',
    cell: ({ row }) => {
      const formatted = format(row.getValue('createdAt'), 'yyyy-MM-dd');
      return <div>{formatted}</div>;
    },
  },
  {
    id: 'actions',
    cell: ({ row }) => {
      const user = row.original;

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
                  window.dispatchEvent(
                    new CustomEvent('edit-user', { detail: user })
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
                Detta kommer att ta bort användaren <strong>{user.name}</strong>
                . Åtgärden går inte att ångra.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Avbryt</AlertDialogCancel>
              <AlertDialogAction
                onClick={async () => {
                  const res = await fetch(`/api/accounts/${user.id}`, {
                    method: 'DELETE',
                  });
                  if (res.status === 204) {
                    toast('Användare borttagen.');
                    window.dispatchEvent(new Event('refresh-users'));
                  } else {
                    toast('Något gick fel när användaren skulle tas bort.');
                  }
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

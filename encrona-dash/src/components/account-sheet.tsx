'use client';

import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useState } from 'react';
import { Session } from 'next-auth';
import { Role } from '@/lib/auth';
import { Company } from '@/app/(dashboard)/(admin)/company/columns';
import { Select, SelectContent, SelectItem, SelectTrigger } from './ui/select';
import { SelectValue } from '@radix-ui/react-select';

interface AccountsSheetProps {
  hideTrigger?: boolean;
  triggerLabel?: string;
  title?: string;
  description?: string;
  confirmLabel?: string;
  companies?: Company[];
  defaultValues?: {
    name?: string;
    email?: string;
    role?: Role;
    companyId?: number;
  };
  onSubmit: (formData: {
    name: string;
    email: string;
    password: string;
    role?: Role;
    companyId?: number;
  }) => void;
  session: Session | null;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function AccountSheet({
  hideTrigger = false,
  triggerLabel = 'Lägg till användare',
  title = 'Ny användare',
  description = 'Fyll i uppgifterna nedan för att skapa en ny användare.',
  confirmLabel = 'Skapa',
  defaultValues = {},
  onSubmit,
  session,
  companies,
  open,
  onOpenChange,
}: AccountsSheetProps) {
  const [name, setName] = useState(defaultValues.name ?? '');
  const [email, setEmail] = useState(defaultValues.email ?? '');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<Role>(defaultValues.role ?? 'USER');
  const [companyId, setCompanyId] = useState(
    defaultValues.companyId ?? undefined
  );

  let requireValues = true;
  if (defaultValues.name || defaultValues.email || defaultValues.role) {
    requireValues = false;
  }

  const isFormValid = name && email && password;
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid && requireValues) return; //TODO add toast here to warn the user

    onSubmit({
      name,
      email,
      password,
      role: session?.user.role === 'ADMIN' ? role : undefined,
      companyId: companyId,
    });

    setPassword('');
  };

  function selectOptions() {
    if (companies != undefined) {
      return companies.map(company => (
        <SelectItem
          key={company['id']}
          value={Number.parseInt(company['id']).toString()}
        >
          {company['name']}
        </SelectItem>
      ));
    } else {
      return (
        <SelectItem key={-1} value={''}>
          {'Hämtar företag...'}
        </SelectItem>
      );
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      {!hideTrigger && (
        <SheetTrigger asChild>
          <Button variant="outline">{triggerLabel}</Button>
        </SheetTrigger>
      )}
      <SheetContent side="right" className="px-4">
        <form
          onSubmit={handleSubmit}
          className="flex flex-1 flex-col justify-between"
        >
          <div>
            <SheetHeader>
              <SheetTitle>{title}</SheetTitle>
              <SheetDescription>{description}</SheetDescription>
            </SheetHeader>

            <div className="grid gap-4 px-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="name" className="text-right">
                  Namn
                </Label>
                <Input
                  id="name"
                  value={name}
                  onChange={e => setName(e.target.value)}
                  className="col-span-3"
                  required={requireValues}
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="email" className="text-right">
                  Email
                </Label>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="col-span-3"
                  required={requireValues}
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="password" className="text-right">
                  Lösenord
                </Label>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="col-span-3"
                  required={requireValues}
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="role" className="text-right">
                  Företag
                </Label>
                <Select
                  key={companyId}
                  name="selectBuildings"
                  value={companyId?.toLocaleString()}
                  onValueChange={(newValue: string) => {
                    setCompanyId(Number.parseInt(newValue));
                  }}
                >
                  <SelectTrigger>
                    <SelectValue
                      aria-label={companyId?.toLocaleString()}
                      placeholder="Välj ett företag"
                    />
                  </SelectTrigger>
                  <SelectContent>{selectOptions()}</SelectContent>
                </Select>
              </div>

              {session?.user.role === 'ADMIN' && (
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="role" className="text-right">
                    Roll
                  </Label>
                  <select
                    id="role"
                    className="col-span-3 rounded-md border px-3 py-2"
                    value={role}
                    onChange={e => setRole(e.target.value as Role)}
                  >
                    <option value="USER">Användare</option>
                    <option value="ADMIN">Administratör</option>
                  </select>
                </div>
              )}
            </div>
          </div>

          <SheetFooter>
            <SheetClose asChild>
              <Button type="submit" disabled={!isFormValid && requireValues}>
                {confirmLabel}
              </Button>
            </SheetClose>
          </SheetFooter>
        </form>
      </SheetContent>
    </Sheet>
  );
}

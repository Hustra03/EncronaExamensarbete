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
import { Company } from '@/app/(dashboard)/(admin)/company/columns';
import { ScrollArea, ScrollBar } from './ui/scroll-area';
import { Checkbox } from './ui/checkbox';

interface BuildingSheetProps {
  hideTrigger?: boolean;
  triggerLabel?: string;
  title?: string;
  description?: string;
  confirmLabel?: string;
  companies?: Company[];
  defaultValues?: {
    name?: string;
    owner?: string;
    installedAt?: string;
    companiesWithAccess?: { id: number; name: string }[];
  };
  onSubmit: (formData: {
    name: string;
    owner: string;
    installedAt: string;
    companiesWithAccess: number[];
  }) => void;
  session: Session | null;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function BuildingSheet({
  hideTrigger = false,
  triggerLabel = 'Lägg till byggnad',
  title = 'Ny byggnad',
  description = 'Fyll i uppgifterna nedan för att skapa en ny byggnad.',
  confirmLabel = 'Skapa',
  defaultValues = {},
  companies,
  onSubmit,
  open,
  onOpenChange,
}: BuildingSheetProps) {
  const [name, setName] = useState(defaultValues.name ?? '');
  const [owner, setOwner] = useState(defaultValues.owner ?? '');

  const [installedAt, setInstalledAt] = useState(
    defaultValues.installedAt?.substring(0, 10) ??
      new Date().toISOString().substring(0, 10)
  );

  const [companiesWithAccess, setCompaniesWithAccess] = useState<number[]>(
    defaultValues.companiesWithAccess?.map(object => {
      return object.id;
    }) ?? []
  );

  function addToCompanyIds(clickEvent: React.MouseEvent) {
    const target = clickEvent.target;
    //This retrives the id from the clicked checkbox, and if it already exists it is removed, and if not it is added to the list of companies to add this to
    if ('id' in target) {
      if (typeof target.id === 'string') {
        const targetId = Number.parseInt(target.id);
        if (companiesWithAccess.includes(targetId)) {
          setCompaniesWithAccess(
            companiesWithAccess.filter(item => item != targetId)
          );
        } else {
          setCompaniesWithAccess([targetId, ...companiesWithAccess]);
        }
      }
    }
  }

  const isFormValid = name && owner && installedAt;

  function ScrollAreaForCompanies() {
    if (companies != undefined) {
      return (
        <ScrollArea className="rounded-md border whitespace-nowrap">
          {companies.map(company => (
            <div
              key={company.id}
              className="mt-5 mb-5 grid grid-cols-5 items-center gap-4"
            >
              <Checkbox
                checked={companiesWithAccess.includes(
                  Number.parseInt(company.id)
                )}
                onClick={addToCompanyIds}
                id={company.id}
              />
              <Label>{company.name}</Label>
            </div>
          ))}
          <ScrollBar />
        </ScrollArea>
      );
    }
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    onSubmit({
      name,
      owner,
      installedAt,
      companiesWithAccess,
    });
  };

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
              <div className="grid grid-cols-5 items-center gap-4">
                <Label htmlFor="name" className="col-span-2 text-right">
                  Namn
                </Label>
                <Input
                  id="name"
                  value={name}
                  onChange={e => setName(e.target.value)}
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-5 items-center gap-4">
                <Label htmlFor="text" className="col-span-2 text-right">
                  Ägare
                </Label>
                <Input
                  id="email"
                  value={owner}
                  onChange={e => setOwner(e.target.value)}
                  className="col-span-3"
                  required
                />
              </div>
              <div className="grid grid-cols-5 items-center gap-4">
                <Label htmlFor="text" className="col-span-2 text-right">
                  Installationsdatum
                </Label>
                <Input
                  id="installedAt"
                  type="date"
                  value={installedAt}
                  onChange={e => setInstalledAt(e.target.value)}
                  className="col-span-3"
                  required
                />
              </div>
              <div>
                <Label htmlFor="text" className="col-span-2 text-right">
                  Företag
                </Label>
                {ScrollAreaForCompanies()}
              </div>
            </div>
          </div>

          <SheetFooter>
            <SheetClose asChild>
              <Button type="submit" disabled={!isFormValid}>
                {confirmLabel}
              </Button>
            </SheetClose>
          </SheetFooter>
        </form>
      </SheetContent>
    </Sheet>
  );
}

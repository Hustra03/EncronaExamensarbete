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

interface BuildingSheetProps {
  hideTrigger?: boolean;
  triggerLabel?: string;
  title?: string;
  description?: string;
  confirmLabel?: string;
  defaultValues?: {
    name?: string;
    owner?: string;
    installedAt?: string;
  };
  onSubmit: (formData: {
    name: string;
    owner: string;
    installedAt: string;
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

  console.log(installedAt);
  const isFormValid = name && owner && installedAt;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;

    onSubmit({
      name,
      owner,
      installedAt,
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
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="name" className="text-right">
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
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="text" className="text-right">
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
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="text" className="text-right">
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

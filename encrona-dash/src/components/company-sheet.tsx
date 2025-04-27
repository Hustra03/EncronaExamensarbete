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

interface CompanySheetProps {
  hideTrigger?: boolean;
  triggerLabel?: string;
  title?: string;
  description?: string;
  confirmLabel?: string;
  defaultValues?: {
    name?: string;
    owner?: string;
  };
  onSubmit: (formData: { name: string; owner: string }) => void;
  session: Session | null;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function CompanySheet({
  hideTrigger = false,
  triggerLabel = 'Lägg till företag',
  title = 'Nytt företag',
  description = 'Fyll i uppgifterna nedan för att skapa ett nytt företag.',
  confirmLabel = 'Skapa',
  defaultValues = {},
  onSubmit,
  open,
  onOpenChange,
}: CompanySheetProps) {
  const [name, setName] = useState(defaultValues.name ?? '');
  const [owner, setOwner] = useState(defaultValues.owner ?? '');

  const isFormValid = name && owner;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;

    onSubmit({
      name,
      owner,
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

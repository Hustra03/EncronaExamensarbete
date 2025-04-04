'use client';

import { loginAction } from '@/app/login/actions';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useActionState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<'form'>) {
  const [status, formAction] = useActionState(loginAction, null);

  const router = useRouter();

  useEffect(() => {
    if (status === 'sucess') router.push('/');
  }, [status, router]);

  return (
    <form
      className={cn('flex flex-col gap-6', className)}
      {...props}
      action={formAction}
    >
      <div className="flex flex-col items-center gap-2 text-center">
        <h1 className="text-2xl font-bold">Logga in på ditt konto</h1>
        <p className="text-muted-foreground text-sm text-balance">
          Ange din email och lösenord för att logga in
        </p>
      </div>

      <div className="grid gap-6">
        <div className="grid gap-3">
          <Label htmlFor="email">Email</Label>
          <Input id="email" name="email" type="email" required />
        </div>
        <div className="grid gap-3">
          <Label htmlFor="password">Lösenord</Label>
          <Input id="password" name="password" type="password" required />
        </div>

        {status && status !== 'sucess' && (
          <p className="text-center text-sm text-red-500">{status}</p>
        )}

        <Button type="submit" className="w-full">
          Login
        </Button>
      </div>
    </form>
  );
}

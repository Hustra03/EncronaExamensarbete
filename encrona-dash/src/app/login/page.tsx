import Image from 'next/image';
import { LoginForm } from '@/components/login-form';

export default function LoginPage() {
  return (
    <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <a href="#" className="flex items-center gap-2 font-medium">
            <div className="text-primary-foreground relative flex size-6 items-center justify-center rounded-md">
              <Image
                src="/Encrona.png"
                alt="Bild på Encronas Loga, vilket är en cirkel med en 1 i sig, med ENCRONA längs med den övre halvan, och El och Automation längs med den nedre halvan"
                fill
                priority
              />
            </div>
            EncronaDash
          </a>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            <LoginForm />
          </div>
        </div>
      </div>
      <div className="bg-muted relative hidden lg:block">
        <Image
          src="/login.jpg"
          alt="Bild på en skog"
          fill
          priority
          className="absolute inset-0 object-cover dark:brightness-[0.75]"
        />
      </div>
    </div>
  );
}

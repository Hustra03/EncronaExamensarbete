'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { useSession } from 'next-auth/react';
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction,
} from '@/components/ui/alert-dialog';
import { toast } from 'sonner';

type NewsItem = {
  id: number;
  title: string;
  content: string;
  createdAt: string;
};

export default function Overview() {
  const [news, setNews] = useState<NewsItem[] | null>(null);
  const [loading, setLoading] = useState(true);
  const { data: session } = useSession();

  const isAdmin = session?.user?.role === 'ADMIN';

  useEffect(() => {
    fetch('/api/news')
      .then(res => res.json())
      .then(data => {
        setNews(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <main className="mx-auto max-w-3xl space-y-10 px-6 py-12">
      <section className="space-y-4">
        <h1 className="text-3xl font-bold text-green-500 dark:text-green-400">
          Välkommen till EncronaDash!
        </h1>
        <p className="text-gray-800 dark:text-gray-200">
          Denna hemsida demonstrerar hur Encrona Metoden skapar besparingar
          genom energieffektivisering.
        </p>
        <Link
          href="https://encrona.se"
          className="text-green-600 underline dark:text-green-400"
        >
          Lär dig mer på Encrona.se
        </Link>
        <p className="mt-6 text-sm text-gray-500 italic dark:text-gray-300">
          Prognoser är skattningar baserat på våra egna beräkningar. Dessa är
          under ständig utveckling och kan avvika från verkligt utfall.
        </p>
      </section>

      <section className="space-y-6">
        <h2 className="text-2xl font-semibold dark:text-white">Nyheter</h2>

        {loading && (
          <div className="space-y-4">
            <Skeleton className="h-6 w-3/4" />
            <Skeleton className="h-6 w-1/2" />
          </div>
        )}

        {!loading && news?.length === 0 && (
          <p className="text-gray-500 dark:text-gray-400">
            Inga nyheter tillgängliga just nu.
          </p>
        )}

        {!loading &&
          news?.map(item => (
            <div
              key={item.id}
              className="space-y-2 border-l-4 border-green-500 pl-4 break-all"
            >
              <div className="flex items-center justify-between">
                <h3 className="text-xl font-medium text-gray-900 dark:text-white">
                  {item.title}
                </h3>
                {isAdmin && (
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button variant="destructive" size="sm">
                        Ta bort
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Är du säker?</AlertDialogTitle>
                        <AlertDialogDescription>
                          Detta kommer att ta bort nyheten. Åtgärden går inte
                          att ångra.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Avbryt</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={async () => {
                            const res = await fetch(`/api/news/${item.id}`, {
                              method: 'DELETE',
                            });
                            if (res.status === 204) {
                              setNews(
                                prev =>
                                  prev?.filter(n => n.id !== item.id) ?? null
                              );
                              toast('Nyhet borttagen.');
                            } else {
                              toast(
                                'Något gick fel när nyheten skulle tas bort.'
                              );
                            }
                          }}
                        >
                          Ta bort
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                )}
              </div>
              <p className="text-gray-700 dark:text-gray-300">{item.content}</p>
            </div>
          ))}
      </section>
    </main>
  );
}

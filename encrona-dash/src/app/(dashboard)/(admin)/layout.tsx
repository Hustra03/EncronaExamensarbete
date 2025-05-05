import { auth, isAdmin } from '@/lib/auth';
import { redirect } from 'next/navigation';

export default async function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const session = await auth();

  if (!isAdmin(session)) redirect('/');

  return <>{children}</>;
}

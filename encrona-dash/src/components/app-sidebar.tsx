import { Sidebar } from '@/components/ui/sidebar';
import { AppSidebarClient } from './app-sidebar-client';
import { Session } from 'next-auth';

export default async function AppSidebar({
  session,
  ...props
}: React.ComponentProps<typeof Sidebar> & {
  session: Session;
}) {
  const user = {
    name: session.user.name,
    email: session.user.email,
    role: session.user.role,
  };

  return <AppSidebarClient {...props} user={user} />;
}

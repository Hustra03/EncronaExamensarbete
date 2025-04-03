import { auth } from "@/lib/auth";
import { Sidebar } from "@/components/ui/sidebar";
import { AppSidebarClient } from "./app-sidebar-client";
import { Session } from "next-auth";

export default async function AppSidebar(
  props: React.ComponentProps<typeof Sidebar>
) {
  const session = (await auth()) as Session;

  const user = {
    name: session.user.name,
    email: session.user.email,
    role: session.user.role,
  };

  return <AppSidebarClient {...props} user={user} />;
}

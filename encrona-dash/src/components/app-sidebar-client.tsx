'use client';

import * as React from 'react';
import {
  HelpCircleIcon,
  SettingsIcon,
  Leaf,
  UserPen,
  HousePlus,
  Building,
  Eye,
  ComputerIcon,
} from 'lucide-react';

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { NavMain } from '@/components/nav-main';
import { NavSecondary } from '@/components/nav-secondary';
import { NavUser } from '@/components/nav-user';
import { Role } from '@/lib/auth';

const sidebar = {
  navMain: [
    {
      title: 'Överblick',
      url: '/',
      icon: Eye,
      isActive: false,
    },
    {
      title: 'Fastigheter',
      icon: Building,
      isActive: false,
      items: [
        {
          title: 'Porslinsfabriken',
          url: '#',
        },
        {
          title: 'Dovhjorten',
          url: '#',
        },
      ],
    },
  ],
  navAdmin: [
    {
      title: 'Användare',
      url: '/accounts',
      icon: UserPen,
    },
    {
      title: 'Fastigheter',
      url: '/buildings',
      icon: HousePlus,
    },
    {
      title: 'Simulation',
      url: 'simulationInput',
      icon: ComputerIcon,
    },
  ],
  navSecondary: [
    {
      title: 'Inställningar',
      url: '#',
      icon: SettingsIcon,
    },
    {
      title: 'Få hjälp',
      url: '#',
      icon: HelpCircleIcon,
    },
  ],
};

export function AppSidebarClient({
  user,
  ...props
}: React.ComponentProps<typeof Sidebar> & {
  user: { name: string; email: string; role: Role };
}) {
  return (
    <Sidebar collapsible="offcanvas" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              asChild
              className="data-[slot=sidebar-menu-button]:!p-1.5"
            >
              <a href="#">
                <Leaf className="h-5 w-5" />
                <span className="text-base font-semibold">EncronaDash</span>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <NavMain
          navApp={sidebar.navMain}
          navAdmin={sidebar.navAdmin}
          isAdmin={user.role === 'ADMIN'}
        />
        <NavSecondary items={sidebar.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={user} />
      </SidebarFooter>
    </Sidebar>
  );
}

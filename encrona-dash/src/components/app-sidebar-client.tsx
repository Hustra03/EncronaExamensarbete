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
  Building2Icon,
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
import { Building as BuildingType } from '@/app/(dashboard)/(admin)/buildings/columns';

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
      url: '/simulationInput',
      icon: ComputerIcon,
    },
    {
      title: 'Företag',
      url: '/company',
      icon: Building2Icon,
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
  const [buildings, setBuildings] = React.useState<BuildingType[]>([]);

  async function fetchBuildings() {
    const res = await fetch('/api/companyBuildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
    }
  }

  React.useEffect(() => {
    fetchBuildings();
  }, []);

  if (buildings) {
    sidebar.navMain[1].items = buildings.map(buildingObject => {
      return {
        title: buildingObject.name,
        url: '/building/' + buildingObject.id,
      };
    });
  }

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

'use client';

import { useEffect, useState } from 'react';
import { columns } from './columns';
import { DataTable } from './data-table';
import type { Building } from './columns';
import { Company } from '../company/columns';

export default function AccountsTable() {
  const [buildings, setBuildings] = useState<Building[]>([]);
  const [loading, setLoading] = useState(true);

  async function fetchBuildings() {
    const res = await fetch('/api/buildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
    }
    setLoading(false);
  }

  const [companies, setCompanies] = useState<Company[]>([]);

  async function fetchCompanies() {
    const res = await fetch('/api/company');
    if (res.ok) {
      const companiesData = await res.json();
      setCompanies(companiesData);
    }
    setLoading(false);
  }

  useEffect(() => {
    fetchBuildings();
    fetchCompanies();
  }, []);

  if (loading) return <div>Laddar byggnader...</div>;

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        companies={companies}
        data={buildings}
        onRefresh={fetchBuildings}
      />
    </div>
  );
}

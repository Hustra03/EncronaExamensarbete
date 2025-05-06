'use client';

import { useEffect, useState } from 'react';
import { columns } from './columns';
import { DataTable } from './data-table';
import type { Company } from './columns';
import Spinner from '@/components/spinner';

export default function AccountsTable() {
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);

  async function fetchCompanies() {
    const res = await fetch('/api/company');
    if (res.ok) {
      const data = await res.json();
      setCompanies(data);
    }
    setLoading(false);
  }

  useEffect(() => {
    fetchCompanies();
  }, []);

  if (loading) return <Spinner />;

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        data={companies}
        onRefresh={fetchCompanies}
      />
    </div>
  );
}

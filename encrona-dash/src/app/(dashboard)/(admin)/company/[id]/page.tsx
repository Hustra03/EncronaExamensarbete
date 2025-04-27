'use client';

import { useEffect, useState } from 'react';
import { columns } from '../../accounts/columns';
import { DataTable } from '../../accounts/data-table';
import { useParams } from 'next/navigation';
import { Company } from '../columns';
import type { User } from '../../accounts/columns';

export default function CompanyAccountsTable() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const { id } = useParams();

  async function fetchUsers() {
    const res = await fetch('/api/companyAccounts/' + id);
    if (res.ok) {
      const data = await res.json();
      setUsers(data);
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
    fetchUsers();
    fetchCompanies();
  }, []);

  if (loading) return <div>Laddar användare...</div>;

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        data={users}
        companies={companies}
        onRefresh={fetchUsers}
      />
    </div>
  );
}

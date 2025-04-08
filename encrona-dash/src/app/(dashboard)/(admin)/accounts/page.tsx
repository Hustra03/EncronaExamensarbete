'use client';

import { useEffect, useState } from 'react';
import { columns } from './columns';
import { DataTable } from './data-table';
import type { User } from './columns';

export default function AccountsTable() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);

  async function fetchUsers() {
    const res = await fetch('/api/accounts');
    if (res.ok) {
      const data = await res.json();
      setUsers(data);
    }
    setLoading(false);
  }

  useEffect(() => {
    fetchUsers();
  }, []);

  if (loading) return <div>Laddar användare...</div>;

  return (
    <div className="container mx-auto py-10">
      <DataTable columns={columns} data={users} onRefresh={fetchUsers} />
    </div>
  );
}

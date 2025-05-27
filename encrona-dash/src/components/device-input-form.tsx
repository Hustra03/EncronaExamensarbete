'use client';

import { useEffect, useState } from 'react';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import Spinner from './spinner';
import { toast } from 'sonner';

type DeviceSource = 'MIVO' | 'BELIMO';

interface Building {
  id: number;
  name: string;
}

interface Device {
  id: number;
  externalId: string;
  source: DeviceSource;
}

export default function DeviceInputForm() {
  const [buildings, setBuildings] = useState<Building[]>([]);
  const [selectedBuildingId, setSelectedBuildingId] = useState<string>('');
  const [externalId, setExternalId] = useState('');
  const [source, setSource] = useState<DeviceSource | ''>('');
  const [linkedDevices, setLinkedDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/buildings')
      .then(res => res.json())
      .then(data => {
        setBuildings(data);
        if (data.length > 0) {
          setSelectedBuildingId(data[0].id.toString());
        }
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    if (!selectedBuildingId) return;
    fetch(`/api/devices/${selectedBuildingId}`)
      .then(res => res.json())
      .then(setLinkedDevices);
  }, [selectedBuildingId]);

  const handleLink = async () => {
    if (!externalId || !source || !selectedBuildingId) return;

    const res = await fetch('/api/devices', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        buildingId: parseInt(selectedBuildingId),
        source,
        externalId,
      }),
    });

    if (res.ok) {
      toast.success('Enhet länkad');
      setExternalId('');
      setSource('');
      const data = await fetch(`/api/devices/${selectedBuildingId}`).then(r =>
        r.json()
      );
      setLinkedDevices(data);
    } else {
      toast.error('Misslyckades att länka enhet');
    }
  };

  const handleRemove = async (id: number) => {
    const res = await fetch(`/api/devices/${id}`, { method: 'DELETE' });
    if (res.ok) {
      toast('Enhet borttagen');
      setLinkedDevices(prev => prev.filter(d => d.id !== id));
    } else {
      toast.error('Kunde inte ta bort enheten');
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="flex flex-col items-center gap-6 p-6">
      <h2 className="text-xl font-semibold">Länka enhet till byggnad</h2>

      <div>
        <Label>Välj byggnad</Label>
        <Select
          value={selectedBuildingId}
          onValueChange={setSelectedBuildingId}
        >
          <SelectTrigger>
            <SelectValue placeholder="Välj byggnad..." />
          </SelectTrigger>
          <SelectContent>
            {buildings.map(b => (
              <SelectItem key={b.id} value={b.id.toString()}>
                {b.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="w-1/4">
        <Label>Externt ID</Label>
        <Input
          value={externalId}
          onChange={e => setExternalId(e.target.value)}
        />
      </div>
      <div>
        <Label>Typ</Label>
        <Select
          value={source}
          onValueChange={v => setSource(v as DeviceSource)}
        >
          <SelectTrigger>
            <SelectValue placeholder="Typ..." />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="MIVO">MIVO</SelectItem>
            <SelectItem value="BELIMO">BELIMO</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Button onClick={handleLink}>Länka</Button>

      <h3 className="mt-4 font-semibold">Länkade enheter</h3>
      {linkedDevices.length === 0 ? (
        <p>Inga enheter länkade.</p>
      ) : (
        <ul className="space-y-2">
          {linkedDevices.map(d => (
            <li key={d.id} className="flex items-center justify-between">
              <span>
                {d.source} – {d.externalId}
              </span>
              <Button
                size="sm"
                variant="destructive"
                onClick={() => handleRemove(d.id)}
              >
                Ta bort
              </Button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

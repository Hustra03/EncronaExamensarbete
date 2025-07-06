'use client';

import { useEffect, useState } from 'react';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from '@/components/ui/select';
import Spinner from './spinner';
import { toast } from 'sonner';

type Building = {
  id: number;
  name: string;
};

const months = [
  { key: 'january', label: 'Januari' },
  { key: 'february', label: 'Februari' },
  { key: 'march', label: 'Mars' },
  { key: 'april', label: 'April' },
  { key: 'may', label: 'Maj' },
  { key: 'june', label: 'Juni' },
  { key: 'july', label: 'Juli' },
  { key: 'august', label: 'Augusti' },
  { key: 'september', label: 'September' },
  { key: 'october', label: 'Oktober' },
  { key: 'november', label: 'November' },
  { key: 'december', label: 'December' },
];

const benchmarkTypes = [
  { value: 'NORMALIZED', label: 'Normalår' },
  { value: 'PRE_ACTION', label: 'Före åtgärd' },
];

const energyTypes = [
  { value: 'SPACE_HEATING', label: 'Uppvärmning' },
  { value: 'WATER_HEATING', label: 'Tappvarmvatten' },
  { value: 'ELECTRICITY', label: 'El' },
  { value: 'WATER_USAGE', label: 'Vatten' },
];

export default function BenchmarkDataForm() {
  const [buildings, setBuildings] = useState<Building[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingData, setLoadingData] = useState(false);
  const [formData, setFormData] = useState<Record<string, string>>({});
  const [selectedBuildingId, setSelectedBuildingId] = useState('');
  const [selectedBenchmarkType, setSelectedBenchmarkType] = useState('');
  const [selectedEnergyType, setSelectedEnergyType] = useState('');

  useEffect(() => {
    async function fetchBuildings() {
      const res = await fetch('/api/buildings');
      if (res.ok) {
        const data = await res.json();
        setBuildings(data);
        setSelectedBuildingId(data[0]?.id.toString() ?? '');
      }
      setLoading(false);
    }

    fetchBuildings();
  }, []);

  useEffect(() => {
    if (selectedBuildingId && selectedBenchmarkType && selectedEnergyType) {
      fetchExistingBenchmark();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedBuildingId, selectedBenchmarkType, selectedEnergyType]);

  async function fetchExistingBenchmark() {
    setLoadingData(true);
    try {
      const res = await fetch(
        `/api/benchmarkData?buildingId=${selectedBuildingId}`
      );
      if (!res.ok) {
        setFormData({});
        return;
      }

      const data = await res.json();

      const match = data.find(
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        (entry: any) =>
          entry.type === selectedBenchmarkType &&
          entry.energyType === selectedEnergyType
      );

      if (match) {
        const filled: Record<string, string> = {};
        months.forEach(({ key }) => {
          filled[key] = match[key]?.toString() ?? '';
        });
        setFormData(filled);
      } else {
        setFormData({});
      }
    } catch (error) {
      console.error('Fel vid hämtning av benchmarkdata:', error);
      setFormData({});
    } finally {
      setLoadingData(false);
    }
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!selectedBuildingId || !selectedBenchmarkType || !selectedEnergyType) {
      toast.error('Alla fält måste vara ifyllda.');
      return;
    }

    const values: Record<number, number> = {};
    months.forEach((month, i) => {
      const num = parseFloat(formData[month.key] || '0');
      values[i + 1] = isNaN(num) ? 0 : num;
    });

    const payload = {
      buildingId: parseInt(selectedBuildingId),
      type: selectedBenchmarkType,
      energyType: selectedEnergyType,
      values,
    };

    const res = await fetch('/api/benchmarkData', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      toast.success('Data sparad.');
    } else {
      toast.error('Något gick fel när datan skulle sparas.');
    }
  }

  if (loading) return <Spinner />;

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-1 flex-col items-center space-y-8"
    >
      <div className="flex flex-wrap justify-center gap-6">
        <div className="flex flex-col items-start gap-1">
          <Label>Byggnad</Label>
          <Select
            value={selectedBuildingId}
            onValueChange={setSelectedBuildingId}
          >
            <SelectTrigger className="w-48">
              <SelectValue placeholder="Välj byggnad..." />
            </SelectTrigger>
            <SelectContent>
              {buildings.map(building => (
                <SelectItem key={building.id} value={building.id.toString()}>
                  {building.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex flex-col items-start gap-1">
          <Label>Typ</Label>
          <Select
            value={selectedBenchmarkType}
            onValueChange={setSelectedBenchmarkType}
          >
            <SelectTrigger className="w-40">
              <SelectValue placeholder="Välj typ..." />
            </SelectTrigger>
            <SelectContent>
              {benchmarkTypes.map(type => (
                <SelectItem key={type.value} value={type.value}>
                  {type.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex flex-col items-start gap-1">
          <Label>Energityp</Label>
          <Select
            value={selectedEnergyType}
            onValueChange={setSelectedEnergyType}
          >
            <SelectTrigger className="w-52">
              <SelectValue placeholder="Välj energityp..." />
            </SelectTrigger>
            <SelectContent>
              {energyTypes.map(type => (
                <SelectItem key={type.value} value={type.value}>
                  {type.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      {loadingData ? (
        <Spinner />
      ) : (
        <>
          <div className="grid w-full max-w-3xl grid-cols-2 gap-6">
            {months.map(({ key, label }) => (
              <div key={key} className="flex flex-col gap-1">
                <Label>{label}</Label>
                <Input
                  type="number"
                  step="any"
                  value={formData[key] || ''}
                  onChange={e =>
                    setFormData(prev => ({
                      ...prev,
                      [key]: e.target.value,
                    }))
                  }
                />
              </div>
            ))}
          </div>

          <Button type="submit" className="mt-6">
            Spara
          </Button>
        </>
      )}
    </form>
  );
}

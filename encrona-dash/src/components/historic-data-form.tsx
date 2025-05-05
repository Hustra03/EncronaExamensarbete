'use client';

import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useEffect, useState } from 'react';

type Building = {
  id: number;
  name: string;
};

export default function HistoricDataForm() {
  const [buildings, setBuildings] = useState<Building[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedBuildingId, setSelectedBuildingId] = useState<string>('');
  const [selectedYear, setSelectedYear] = useState<string>('');
  const [selectedMonth, setSelectedMonth] = useState<string>('');
  const [formData, setFormData] = useState<Record<string, string>>({});

  async function fetchBuildings() {
    const res = await fetch('/api/buildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
      setSelectedBuildingId(data[0]?.id.toString() ?? '');
    }
    setLoading(false);
  }

  useEffect(() => {
    fetchBuildings();
  }, []);

  async function fetchExistingData(
    buildingId: string,
    year: string,
    month: string
  ) {
    if (!buildingId || !year || !month) return;

    const res = await fetch(
      `/api/historicData?buildingId=${buildingId}&year=${year}&month=${month}`
    );
    if (res.status === 200) {
      const existing = await res.json();
      setFormData({
        totalEnergykWh: existing.totalEnergykWh?.toString() ?? '',
        spaceHeatingkWh: existing.spaceHeatingkWh?.toString() ?? '',
        waterHeatingkWh: existing.waterHeatingkWh?.toString() ?? '',
        electricitykWh: existing.electricitykWh?.toString() ?? '',
        totalWaterM3: existing.totalWaterM3?.toString() ?? '',
        totalEnergyCost: existing.totalEnergyCost?.toString() ?? '',
        spaceHeatingCost: existing.spaceHeatingCost?.toString() ?? '',
        waterHeatingCost: existing.waterHeatingCost?.toString() ?? '',
        electricityCost: existing.electricityCost?.toString() ?? '',
        totalWaterCost: existing.totalWaterCost?.toString() ?? '',
      });
    } else {
      setFormData({});
    }
  }

  useEffect(() => {
    if (selectedBuildingId && selectedYear && selectedMonth) {
      fetchExistingData(selectedBuildingId, selectedYear, selectedMonth);
    }
  }, [selectedBuildingId, selectedYear, selectedMonth]);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    if (!selectedBuildingId || !selectedYear || !selectedMonth) {
      return;
    }

    const date = new Date(
      Date.UTC(Number(selectedYear), Number(selectedMonth) - 1, 1, 0, 0, 0, 0)
    );

    const data = {
      buildingId: parseInt(selectedBuildingId),
      date: date.toISOString(),
      ...Object.fromEntries(
        Object.entries(formData).map(([key, value]) => [
          key,
          value !== '' ? parseFloat(value) : null,
        ])
      ),
    };

    await fetch('/api/historicData', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    setFormData({});
    setSelectedMonth('');
    setSelectedYear('');
  }

  if (loading) {
    return <div>Laddar byggnader...</div>;
  }

  const currentYear = new Date().getFullYear();
  const startYear = 2024;
  const yearOptions = Array.from(
    { length: currentYear - startYear + 1 },
    (_, i) => currentYear - i
  );

  const monthOptions = [
    { value: '1', label: 'Januari' },
    { value: '2', label: 'Februari' },
    { value: '3', label: 'Mars' },
    { value: '4', label: 'April' },
    { value: '5', label: 'Maj' },
    { value: '6', label: 'Juni' },
    { value: '7', label: 'Juli' },
    { value: '8', label: 'Augusti' },
    { value: '9', label: 'September' },
    { value: '10', label: 'Oktober' },
    { value: '11', label: 'November' },
    { value: '12', label: 'December' },
  ];

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
            onValueChange={value => setSelectedBuildingId(value)}
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
          <Label>År</Label>
          <Select
            value={selectedYear}
            onValueChange={value => setSelectedYear(value)}
          >
            <SelectTrigger className="w-32">
              <SelectValue placeholder="Välj år..." />
            </SelectTrigger>
            <SelectContent>
              {yearOptions.map(year => (
                <SelectItem key={year} value={year.toString()}>
                  {year}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex flex-col items-start gap-1">
          <Label>Månad</Label>
          <Select
            value={selectedMonth}
            onValueChange={value => setSelectedMonth(value)}
          >
            <SelectTrigger className="w-40">
              <SelectValue placeholder="Välj månad..." />
            </SelectTrigger>
            <SelectContent>
              {monthOptions.map(month => (
                <SelectItem key={month.value} value={month.value}>
                  {month.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="flex w-full max-w-5xl flex-col gap-8">
        {[
          {
            consumption: 'spaceHeatingkWh',
            cost: 'spaceHeatingCost',
            label: 'Uppvärmning (kWh & kr)',
          },
          {
            consumption: 'waterHeatingkWh',
            cost: 'waterHeatingCost',
            label: 'Tappvarmvatten (kWh & kr)',
          },
          {
            consumption: 'electricitykWh',
            cost: 'electricityCost',
            label: 'El (kWh & kr)',
          },
          {
            consumption: 'totalWaterM3',
            cost: 'totalWaterCost',
            label: 'Vattenförbrukning (m³ & kr)',
          },
        ].map(({ consumption, cost, label }) => (
          <div key={consumption} className="flex flex-col gap-2">
            <Label>{label}</Label>
            <div className="flex gap-4">
              <Input
                id={consumption}
                type="number"
                step="any"
                placeholder="Förbrukning"
                value={formData[consumption] || ''}
                onChange={e =>
                  setFormData(prev => ({
                    ...prev,
                    [consumption]: e.target.value,
                  }))
                }
              />
              <Input
                id={cost}
                type="number"
                step="any"
                placeholder="Kostnad"
                value={formData[cost] || ''}
                onChange={e =>
                  setFormData(prev => ({
                    ...prev,
                    [cost]: e.target.value,
                  }))
                }
              />
            </div>
          </div>
        ))}
      </div>

      <Button className="mt-8" type="submit">
        Spara data
      </Button>
    </form>
  );
}

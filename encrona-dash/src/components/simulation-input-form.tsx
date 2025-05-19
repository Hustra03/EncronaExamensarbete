'use client';

import { useActionState, useEffect, useState } from 'react';
import { handleSubmit } from '@/app/(dashboard)/(admin)/simulationInput/actions';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/text-area';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import Spinner from './spinner';
import { toast } from 'sonner';
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction,
} from '@/components/ui/alert-dialog';
import { Flame, Zap, Droplets, Waves } from 'lucide-react';

type PriceForecast = {
  id: number;
  buildingId: number;
  dateFrom: string;
  type: 'HEATING' | 'ELECTRICITY' | 'WATER_HEATING' | 'WATER_USAGE';
  price: number;
  createdAt: string;
  updatedAt: string;
};

const translateType = (type: string) => {
  switch (type) {
    case 'HEATING':
      return 'Uppvärmning';
    case 'ELECTRICITY':
      return 'El';
    case 'WATERHEATING':
      return 'Tappvarmvatten';
    case 'WATERUSAGE':
      return 'Vattenförbrukning';
    default:
      return type;
  }
};

const currentDate = new Date();
const forecastOptions = Array.from({ length: 12 }, (_, i) => {
  const date = new Date(
    Date.UTC(currentDate.getFullYear(), currentDate.getMonth() + i, 1)
  );
  return {
    year: date.getUTCFullYear().toString(),
    month: (date.getUTCMonth() + 1).toString(),
    label: date.toLocaleString('sv-SE', { month: 'long', year: 'numeric' }),
  };
});

export function SimulationInputForm({}: React.ComponentProps<'form'>) {
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedBuildingValue, setSelectedBuildingValue] = useState('');
  const [textArea, setTextArea] = useState('');

  const [year, setYear] = useState('');
  const [month, setMonth] = useState('');
  const [type, setType] = useState('');
  const [price, setPrice] = useState('');
  const [forecasts, setForecasts] = useState<PriceForecast[]>([]);

  const [functionReturnValue, formAction] = useActionState(handleSubmit, null);

  useEffect(() => {
    async function fetchBuildings() {
      const res = await fetch('/api/buildings');
      if (res.ok) {
        const data = await res.json();
        setBuildings(data);
        if (data.length > 0) {
          setSelectedBuildingValue(data[0].id.toString());
        }
      }
      setLoading(false);
    }

    fetchBuildings();
  }, []);

  useEffect(() => {
    if (functionReturnValue === 'ok') {
      toast('Simulationsresultat sparat');
      setTextArea('');
    } else if (functionReturnValue && functionReturnValue !== 'ok') {
      toast.error(functionReturnValue.toString());
    }
  }, [functionReturnValue]);

  useEffect(() => {
    if (!selectedBuildingValue) return;
    fetch(`/api/priceForecasts/${selectedBuildingValue}`)
      .then(res => res.json())
      .then(setForecasts);
  }, [selectedBuildingValue]);

  async function handlePriceSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const date = new Date(Date.UTC(Number(year), Number(month) - 1, 1));
    console.log(selectedBuildingValue);

    const payload = {
      buildingId: parseInt(selectedBuildingValue),
      date: date.toISOString(),
      type,
      price: parseFloat(price),
    };

    const res = await fetch('/api/priceForecasts', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      toast('Prisprognos sparad');
      const data = await fetch(
        `/api/priceForecasts/${selectedBuildingValue}`
      ).then(res => res.json());
      setForecasts(data);
    } else {
      toast.error('Kunde inte spara prognosen');
    }
  }

  async function handleDelete(id: number) {
    const res = await fetch(`/api/priceForecasts/${id}`, { method: 'DELETE' });
    if (res.ok) {
      setForecasts(prev => prev.filter(f => f.id !== id));
      toast('Prisprognos borttagen.');
    } else {
      toast.error('Kunde inte ta bort prognosen.');
    }
  }

  if (loading) return <Spinner />;

  const groupedForecasts: Record<string, PriceForecast[]> =
    forecasts.length > 0
      ? forecasts.reduce((acc: Record<string, PriceForecast[]>, forecast) => {
          const dateKey = forecast.dateFrom;
          if (!acc[dateKey]) acc[dateKey] = [];
          acc[dateKey].push(forecast);
          return acc;
        }, {})
      : {};

  const typeIcon = (type: string) => {
    switch (type) {
      case 'HEATING':
        return <Flame className="mr-2 h-4 w-4 text-orange-500" />;
      case 'ELECTRICITY':
        return <Zap className="mr-2 h-4 w-4 text-yellow-500" />;
      case 'WATERHEATING':
        return <Droplets className="mr-2 h-4 w-4 text-blue-400" />;
      case 'WATERUSAGE':
        return <Waves className="mr-2 h-4 w-4 text-cyan-600" />;
      default:
        return null;
    }
  };

  return (
    <div className="flex flex-col items-center gap-12 p-6">
      <div className="flex w-full max-w-3xl flex-wrap justify-center gap-6">
        <div className="flex flex-col items-start gap-1">
          <Label htmlFor="selectBuildings">Välj byggnad</Label>
          <Select
            name="selectBuildings"
            value={selectedBuildingValue}
            onValueChange={setSelectedBuildingValue}
          >
            <SelectTrigger className="w-64">
              <SelectValue placeholder="Välj byggnad..." />
            </SelectTrigger>
            <SelectContent>
              {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
              {buildings.map((b: any) => (
                <SelectItem key={b.id} value={b.id.toString()}>
                  {b.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <form
        action={formAction}
        className="flex w-full max-w-3xl flex-col items-center gap-4"
      >
        <h2 className="text-lg font-semibold">1. Simuleringsresultat</h2>
        <input
          type="hidden"
          name="selectBuildings"
          value={selectedBuildingValue}
        />
        <div className="w-full">
          <Textarea
            id="simulationResults"
            name="simulationResults"
            required
            className="min-h-[180px]"
            value={textArea}
            onChange={e => setTextArea(e.target.value)}
          />
        </div>
        <Button type="submit" className="mt-2">
          Spara simulering
        </Button>
      </form>

      <hr className="w-full max-w-3xl border-t border-gray-300" />

      <form
        onSubmit={handlePriceSubmit}
        className="flex w-full max-w-3xl flex-col items-center gap-4"
      >
        <h2 className="text-lg font-semibold">2. Prisprognos</h2>
        <div className="flex flex-wrap gap-6">
          <div className="flex flex-col gap-1">
            <Label>Månad</Label>
            <Select
              value={`${year}-${month}`}
              onValueChange={value => {
                const [y, m] = value.split('-');
                setYear(y);
                setMonth(m);
              }}
            >
              <SelectTrigger className="w-72">
                <SelectValue placeholder="Välj månad..." />
              </SelectTrigger>
              <SelectContent>
                {forecastOptions.map(opt => (
                  <SelectItem
                    key={`${opt.year}-${opt.month}`}
                    value={`${opt.year}-${opt.month}`}
                  >
                    {opt.label.charAt(0).toUpperCase() + opt.label.slice(1)}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="flex flex-col gap-1">
            <Label>Typ</Label>
            <Select value={type} onValueChange={setType}>
              <SelectTrigger className="w-56">
                <SelectValue placeholder="Välj typ..." />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="HEATING">Uppvärmning</SelectItem>
                <SelectItem value="ELECTRICITY">El</SelectItem>
                <SelectItem value="WATERHEATING">Tappvarmvatten</SelectItem>
                <SelectItem value="WATERUSAGE">Vattenförbrukning</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex flex-col gap-1">
            <Label>Pris (kr/enhet)</Label>
            <Input
              type="number"
              step="any"
              value={price}
              onChange={e => setPrice(e.target.value)}
              className="w-32"
              placeholder="t.ex. 1.25"
            />
          </div>
        </div>
        <Button type="submit" className="mt-8">
          Spara prisprognos
        </Button>
      </form>

      <section className="w-full max-w-3xl space-y-6">
        <h2 className="text-2xl font-semibold dark:text-white">
          Existerande prisprognoser
        </h2>
        {Object.keys(groupedForecasts).length === 0 ? (
          <p className="text-gray-500 dark:text-gray-400">
            Inga prisprognoser tillgängliga för denna byggnad.
          </p>
        ) : (
          Object.entries(groupedForecasts).map(([dateKey, list]) => (
            <div
              key={dateKey}
              className="space-y-2 border-l-4 border-green-500 pl-4"
            >
              <div className="font-medium text-gray-800 dark:text-white">
                {new Date(dateKey)
                  .toLocaleDateString('sv-SE', {
                    year: 'numeric',
                    month: 'long',
                  })
                  .replace(/^\w/, c => c.toUpperCase())}
              </div>
              <div className="space-y-1">
                {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
                {list.map((item: any) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between"
                  >
                    <div className="flex items-center text-sm text-gray-800 dark:text-white">
                      {typeIcon(item.type)} {translateType(item.type)} —{' '}
                      {item.price} kr
                    </div>
                    <AlertDialog>
                      <AlertDialogTrigger asChild>
                        <Button variant="destructive" size="sm">
                          Ta bort
                        </Button>
                      </AlertDialogTrigger>
                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>Är du säker?</AlertDialogTitle>
                          <AlertDialogDescription>
                            Detta kommer att ta bort prisprognosen. Åtgärden går
                            inte att ångra.
                          </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                          <AlertDialogCancel>Avbryt</AlertDialogCancel>
                          <AlertDialogAction
                            onClick={() => handleDelete(item.id)}
                          >
                            Ta bort
                          </AlertDialogAction>
                        </AlertDialogFooter>
                      </AlertDialogContent>
                    </AlertDialog>
                  </div>
                ))}
              </div>
            </div>
          ))
        )}
      </section>
    </div>
  );
}

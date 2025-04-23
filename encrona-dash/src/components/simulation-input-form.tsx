'use client';

import { handleSubmit } from '@/app/(dashboard)/(admin)/simulationInput/actions';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useActionState, useEffect, useState } from 'react';
import { Textarea } from './ui/text-area';

export function SimulationInputForm({}: React.ComponentProps<'form'>) {
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [functionReturnValue, formAction] = useActionState(handleSubmit, null);

  async function fetchBuildings() {
    const res = await fetch('/api/buildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
    }
    setLoading(false);
  }

  useEffect(() => {
    fetchBuildings();
  }, []);

  if (loading) {
    return <div>Laddar byggnader</div>;
  }

  const selectOptions = buildings.map(building => (
    <SelectItem
      key={building['id']}
      value={Number.parseInt(building['id']).toString()}
    >
      {building['name']}
    </SelectItem>
  ));

  return (
    <form method="post" id="simulationForm" action={formAction}>
      <div className="grid gap-3">
        <Label htmlFor="selectBuildings">Välj Byggnad : </Label>
        <Select name="selectBuildings">
          <SelectTrigger>
            <SelectValue placeholder="Select a building…" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>{selectOptions}</SelectGroup>
          </SelectContent>
        </Select>
      </div>
      <div className="grid gap-3">
        <Label htmlFor="simulationResults">Simulationsresultatet</Label>
        <Textarea id="simulationResults" name="simulationResults" required />
      </div>
      <hr />
      {functionReturnValue && functionReturnValue !== 'ok' && (
        <p className="text-center text-sm text-red-500">
          {functionReturnValue}
        </p>
      )}
      <button type="submit">Submit</button>
    </form>
  );
}

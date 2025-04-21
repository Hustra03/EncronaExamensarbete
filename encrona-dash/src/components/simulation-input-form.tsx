'use client';

import { Input } from '@/components/ui/input';
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

export function SimulationInputForm({}: React.ComponentProps<'form'>) {
  const [buildings, setBuildings] = useState<{ id: number; name: string }[]>(
    []
  );
  const [loading, setLoading] = useState(true);

  async function fetchBuildings() {
    const res = await fetch('/api/buildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
    }
    setLoading(false);
  }

  function handleSubmit(_: unknown, formData: FormData) {
    const index = formData.get('selectBuildings') as string;
    const building = buildings[Number.parseInt(index)];
    const id = building.id;
    const simulationResults = formData.get('simulationResults') as string;
    console.log(id);
    console.log(simulationResults);

    fetch('/api/simulationResults', {
      method: 'Post',
      body: JSON.stringify({ id, simulationResults }),
    });
  }

  const [submitFunction, formAction] = useActionState(handleSubmit, null);

  useEffect(() => {
    fetchBuildings();
  }, []);

  if (loading) {
    return <div>Laddar byggnader</div>;
  }

  const selectOptions = buildings.map((building, index) => (
    <SelectItem key={building['id']} value={index.toString()}>
      {building['name']}
    </SelectItem>
  ));

  return (
    <form method="post" action={formAction}>
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
        <Input
          id="simulationResults"
          name="simulationResults"
          type="text"
          required
        />
      </div>
      <hr />
      <button type="submit">Submit</button>
    </form>
  );
}

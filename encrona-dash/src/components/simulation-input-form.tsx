'use client';

import { handleSubmit } from '@/app/(dashboard)/(admin)/simulationInput/actions';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useActionState, useEffect, useState } from 'react';
import { Textarea } from './ui/text-area';

export function SimulationInputForm({}: React.ComponentProps<'form'>) {
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedBuildingValue, setSelectedBuildingValue] = useState('4');
  const [textArea, setTextArea] = useState('');

  const [functionReturnValue, formAction] = useActionState(handleSubmit, null);

  async function fetchBuildings() {
    const res = await fetch('/api/buildings');
    if (res.ok) {
      const data = await res.json();
      setBuildings(data);
      setSelectedBuildingValue(Number.parseInt(data[0]['id']).toString());
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

  function handleTextAreaChange(e: React.ChangeEvent<HTMLTextAreaElement>) {
    setTextArea(e.target.value);
  }

  return (
    <form method="post" id="simulationForm" action={formAction}>
      <div className="grid gap-3">
        <Label htmlFor="selectBuildings">Välj Byggnad : </Label>
        <Select
          key={selectedBuildingValue}
          name="selectBuildings"
          value={selectedBuildingValue}
          onValueChange={newValue => {
            setSelectedBuildingValue(newValue);
          }}
        >
          <SelectTrigger>
            <SelectValue
              aria-label={selectedBuildingValue}
              placeholder="Select a building…"
            />
          </SelectTrigger>
          <SelectContent>{selectOptions}</SelectContent>
        </Select>
      </div>
      <div className="grid gap-3">
        <Label htmlFor="simulationResults">Simulationsresultatet</Label>
        <Textarea
          id="simulationResults"
          name="simulationResults"
          required
          value={textArea}
          onChange={handleTextAreaChange}
        />
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

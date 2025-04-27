import { SimulationInputForm } from '@/components/simulation-input-form';

export default async function SimulationInput() {
  return (
    <div className="flex flex-1 items-center justify-center">
      <div className="w-full max-w-xs">
        <SimulationInputForm />
      </div>
    </div>
  );
}

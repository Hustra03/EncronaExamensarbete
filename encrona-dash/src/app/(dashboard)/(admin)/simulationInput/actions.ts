export async function handleSubmit(_: unknown, formData: FormData) {
    const id = formData.get('selectBuildings') as string;
    const simulationResults = formData.get('simulationResults') as string;
    console.log(id);
    console.log(simulationResults);

    fetch('/api/simulationResults', {
      method: 'Post',
      body: JSON.stringify({ id, simulationResults }),
    });
  }
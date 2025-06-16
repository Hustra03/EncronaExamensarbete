'use client';

export async function handleSubmit(_: unknown, formData: FormData) {
  const id = formData.get('selectBuildings') as string;
  const simulationResults = formData.get('simulationResults') as string;

  const response = await fetch('/api/simulationResults', {
    method: 'Post',
    body: JSON.stringify({ id, simulationResults }),
  });

  if (response.status.toLocaleString().includes('204')||response.status.toLocaleString().includes('201')) {
    return 'ok';
  } else {
    const jsonValue = await response.json();
    return jsonValue['message'];
  }
}

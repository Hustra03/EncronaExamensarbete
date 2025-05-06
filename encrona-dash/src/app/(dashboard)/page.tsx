import Link from 'next/link';

const newsItems = [
  {
    title: 'Nyhet: Energibesparingar i fokus',
    text: 'Encrona hjälper fastighetsägare att spara energi genom smarta uppgraderingar av ventilation, värme och isolering.',
  },
  {
    title: 'Nyhet: Nya simuleringsverktyg',
    text: 'Vi har förbättrat våra modeller för att ge ännu mer träffsäkra prognoser för energibesparing.',
  },
];

export default async function Overview() {
  return (
    <main className="mx-auto max-w-3xl space-y-10 px-6 py-12">
      <section className="space-y-4">
        <h1 className="text-3xl font-bold text-green-600">
          Välkommen till EncronaDash
        </h1>
        <p>
          Denna hemsida demonstrerar hur Encrona Metoden skapar besparingar
          genom energieffektivisering.
        </p>
        <Link href="https://encrona.se" className="text-green-600 underline">
          Lär dig mer på Encrona.se
        </Link>
      </section>

      <section className="space-y-6">
        <h2 className="text-2xl font-semibold">Nyheter</h2>
        {newsItems.map((item, i) => (
          <div key={i} className="border-l-4 border-green-500 pl-4">
            <h3 className="text-xl font-medium">{item.title}</h3>
            <p className="text-gray-700">{item.text}</p>
          </div>
        ))}
      </section>
    </main>
  );
}

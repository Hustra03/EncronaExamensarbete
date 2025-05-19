export async function getBelimoAccessToken(): Promise<string> {
  console.log(process.env.BELIMO_PASSWORD);
  console.log(process.env.BELIMO_USERNAME);
  const res = await fetch('https://id.belimo.com/oauth/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      grant_type: 'password',
      client_id: process.env.BELIMO_CLIENT_ID!,
      client_secret: process.env.BELIMO_CLIENT_SECRET!,
      username: process.env.BELIMO_USERNAME!,
      password: process.env.BELIMO_PASSWORD!,
      audience: 'https://api.cloud.belimo.com/',
      scope: 'public.read',
    }),
  });

  if (!res.ok) {
    const error = await res.text();
    console.error('Belimo token error:', error);
    throw new Error('Belimo token fetch failed');
  }

  const json = await res.json();
  return json.access_token;
}

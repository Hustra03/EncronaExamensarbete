'use server';

import { signIn } from '@/lib/auth';

export async function loginAction(_: unknown, formData: FormData) {
  const email = formData.get('email') as string;
  const password = formData.get('password') as string;

  try {
    const res = await signIn('credentials', {
      email,
      password,
      redirect: false,
    });

    if (res?.error) {
      return 'Fel e-post eller lösenord';
    }

    return 'sucess';
  } catch (error) {
    console.error('Login error:', JSON.stringify(error, null, 2));
    return 'Något gick fel';
  }
}

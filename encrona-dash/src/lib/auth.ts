import NextAuth, { Session } from 'next-auth';
/* eslint-disable @typescript-eslint/no-unused-vars */
import type { JWT } from 'next-auth/jwt';
import Credentials from 'next-auth/providers/credentials';
import { PrismaClient } from '@prisma/client';
import { compare } from 'bcryptjs';

export type Role = 'USER' | 'ADMIN';

declare module 'next-auth' {
  interface Session {
    user: {
      id: string;
      email: string;
      name: string;
      role: Role;
    };
  }

  interface User {
    role: Role;
  }
}

declare module 'next-auth/jwt' {
  /** Returned by the `jwt` callback and `auth`, when using JWT sessions */
  interface JWT {
    id: string;
    role: Role;
  }
}

const prismaClient = new PrismaClient();

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [
    Credentials({
      name: 'Credentials',
      credentials: {
        email: {},
        password: {},
      },
      async authorize(credentials) {
        const email = credentials?.email as string;
        const password = credentials?.password as string;

        if (!email || !password) {
          return null;
        }

        const user = await prismaClient.user.findUnique({
          where: { email },
        });

        if (!user || !user.password) {
          return null;
        }

        const isValid = await compare(password, user.password);
        if (!isValid) {
          return null;
        }

        return {
          id: user.id.toString(),
          email: user.email,
          name: user.name,
          role: user.role,
        };
      },
    }),
  ],
  callbacks: {
    jwt({ token, user }) {
      if (user&&user.id) {
        token.id = user.id;
        token.role = user.role;
      }
      return token;
    },
    session({ session, token }) {
      session.user.id = token.id;
      session.user.role = token.role;
      return session;
    },
  },
  session: {
    strategy: 'jwt',
  },
  secret: process.env.NEXTAUTH_SECRET,
});

export function isAdmin(session: Session | null | undefined): boolean {
  return session?.user?.role === 'ADMIN';
}

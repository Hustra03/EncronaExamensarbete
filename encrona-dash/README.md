# Encrona Dashboard

A web-based dashboard for visualizing and comparing estimated vs. actual energy usage data in Swedish apartment buildings. The dashboard is part of a larger energy efficiency estimator system created for Encrona El & Automation.

## Features

- Authenticated access (NextAuth.js with credentials provider)
- Interactive visualizations of electricity, heating, and water usage
- Monthly consumption graphs per building and resource
- Overlay of simulated estimates and real measurements
- Admin functionality for approving building access
- Lightweight estimate generation using heuristic curves

## Tech Stack

- **Framework:** Next.js
- **Auth:** NextAuth.js (credentials provider)
- **Database:** PostgreSQL + Prisma
- **UI:** Tailwind CSS + shadcn/ui + Recharts
- **Deployment:** Vercel
- **Data integration:** API endpoints for MIVO and Belimo

## Setup

1. **Install**

```bash
cd encrona-dash
npm install
```

2. **Create `.env`**

```bash
cp .env.sample .env
```

Fill in required values:

```env
DATABASE_URL=...
AUTH_SECRET=...
CRON_SECRET=...
MIVO_TOKEN=...
BELIMO_CLIENT_ID=...
BELIMO_CLIENT_SECRET=...
BELIMO_USERNAME=...
BELIMO_PASSWORD=...
```

3. **Set up the database**

```bash
npx prisma generate
```

4. **Start development**

```bash
npm run dev
```

Dashboard runs at `http://localhost:3000`.

## Estimate Workflow

- When a user accesses a building without data:
  - Estimates are generated dynamically based on yearly values and predefined monthly curves.

## Developer Workflow

Before committing:

```bash
npm run format   # formats using Prettier and sorts Tailwind classes
npm run lint     # checks for TypeScript and style errors
```

## Prisma ORM

- Prisma is used to define and query the database schema via `schema.prisma`.
- To apply changes:

```bash
npm run prisma
```

Migrations are stored under `/migrations`

## Authentication (NextAuth.js)

- Uses **credentials provider** (email/password)
- Registration is handled separately in `src/lib/auth.ts`
- Passwords are hashed and salted
- Session is stored in a secure JWT cookie

## Tailwind + shadcn

- Tailwind is used for styling components directly in JSX
- Shadcn is used for modular UI components
- No external dependencies are required – only copy used components into `/src/components`

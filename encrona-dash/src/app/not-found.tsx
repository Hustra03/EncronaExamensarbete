import { auth } from "@/lib/auth";
import { redirect } from "next/navigation";

export default async function NotFound() {
  const session = await auth();

  if (!session) redirect("/login");
  else redirect("/");
}

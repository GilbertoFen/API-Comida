import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AppFoodSpring API Console",
  description: "Consola simple en Next.js para probar los endpoints del backend Spring Boot.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}

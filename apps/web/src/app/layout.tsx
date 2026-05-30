import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AppFoodSpring",
  description: "Seguimiento y exploracion de alimentos y recetas conectadas a Spring Boot.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}

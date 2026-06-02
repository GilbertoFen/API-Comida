import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AppFoodSpring",
  description: "Health and fitness tracker conectado al backend Spring Boot de AppFoodSpring.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}

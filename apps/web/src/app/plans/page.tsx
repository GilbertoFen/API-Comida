import { PlaceholderScreen } from "@/components/product/placeholder-screen";

export default function PlansPage() {
  return (
    <PlaceholderScreen
      badge="Planes"
      title="La vista de planes sigue en preparacion."
      description="La especificacion pide una ruta viva para evitar enlaces rotos, asi que dejamos este placeholder conectado al flujo actual."
      primaryHref="/"
      primaryLabel="Volver a la landing"
      secondaryHref="/register"
      secondaryLabel="Ir al onboarding"
    />
  );
}

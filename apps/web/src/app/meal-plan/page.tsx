import { PlaceholderScreen } from "@/components/product/placeholder-screen";

export default function MealPlanPage() {
  return (
    <PlaceholderScreen
      badge="Meal Plan"
      title="Tu dia completo vivira aqui."
      description="La card de nutricion ya redirige a esta pantalla placeholder para cumplir la navegacion esperada mientras llega el modulo final."
      primaryHref="/home"
      primaryLabel="Volver al home"
      secondaryHref="/api-testv1"
      secondaryLabel="Ver endpoints"
    />
  );
}

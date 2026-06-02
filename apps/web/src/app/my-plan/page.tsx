import { PlaceholderScreen } from "@/components/product/placeholder-screen";

export default function MyPlanPage() {
  return (
    <PlaceholderScreen
      badge="Mi plan"
      title="Tu plan personalizado aparecera aqui."
      description="Por ahora esta pantalla funciona como destino seguro para navbar y sidebar mientras el modulo final se construye."
      primaryHref="/home"
      primaryLabel="Volver al home"
      secondaryHref="/meal-plan"
      secondaryLabel="Ver meal plan"
    />
  );
}

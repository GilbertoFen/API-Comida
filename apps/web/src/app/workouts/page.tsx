import { PlaceholderScreen } from "@/components/product/placeholder-screen";

export default function WorkoutsPage() {
  return (
    <PlaceholderScreen
      badge="Entrenamientos"
      title="El modulo de entrenamientos tendra su pantalla dedicada."
      description="La navegacion ya lo contempla aunque de momento el home solo muestra un resumen."
      primaryHref="/home"
      primaryLabel="Volver al home"
      secondaryHref="/api-testv1"
      secondaryLabel="Configurar endpoints"
    />
  );
}

import { PlaceholderScreen } from "@/components/product/placeholder-screen";

export default function ProfilePage() {
  return (
    <PlaceholderScreen
      badge="Perfil"
      title="La pantalla de perfil sigue pendiente."
      description="El navbar privado y el sidebar ya pueden redirigir aqui sin dejar una ruta rota."
      primaryHref="/home"
      primaryLabel="Volver al home"
      secondaryHref="/register"
      secondaryLabel="Editar onboarding"
    />
  );
}

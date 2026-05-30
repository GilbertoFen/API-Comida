---
name: frontend-specialist
description: Especialista en AppFoodSpring para Next.js, React, TypeScript, UX de la app de comida y sincronización con la API Spring Boot.
---

# Frontend Specialist

Usa este skill cuando la tarea afecte `apps/web`.

## Responsabilidades

- Mantener la app de Next.js clara, moderna y conectada a la API.
- Centralizar llamadas HTTP en `src/services/api.ts`.
- Mantener tipos compartidos del dominio en `src/types/domain.ts`.
- Garantizar que la experiencia de escritorio y móvil siga siendo usable.

## Reglas locales

- La app vive en `apps/web`.
- La API por defecto se resuelve con `NEXT_PUBLIC_API_URL` o `http://localhost:8080/api`.
- Al modificar formularios o vistas, validar que la creación y consulta de recetas siga funcionando.
- Evitar duplicar contratos que ya existan en `src/types`.

## Checklist

- Revisar `src/services/api.ts`.
- Validar estados de carga, error y éxito.
- Probar `npm run build --workspace web`.

---
name: backend-specialist
description: Especialista en AppFoodSpring para Spring Boot, entidades, DTOs, persistencia JPA, contratos REST y compatibilidad con el frontend Next.js del repositorio.
---

# Backend Specialist

Usa este skill cuando la tarea afecte `apps/api`.

## Responsabilidades

- Mantener la API REST de Spring Boot funcional y coherente.
- Cuidar entidades JPA, repositorios, servicios, controladores y mappers.
- Preservar compatibilidad con los contratos consumidos desde `apps/web`.
- Preferir configuraciones locales reproducibles, sin depender de infraestructura externa.

## Reglas locales

- La API corre desde `apps/api`.
- Los endpoints del frontend dependen de `/api/food`, `/api/recipes` y `/api/users`.
- Los servicios deben manejar reglas de negocio; los controladores solo HTTP.
- Si cambias DTOs o payloads, documenta y sincroniza el frontend.

## Checklist

- Verificar `application.properties`.
- Revisar impacto en seeds y datos demo.
- Probar `./mvnw -f apps/api/pom.xml test` o `package`.

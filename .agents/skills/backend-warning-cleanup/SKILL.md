---
name: backend-warning-cleanup
description: Corrige warnings de compilacion, detalles de formato y configuraciones menores en el backend Spring Boot de AppFoodSpring sin cambiar la logica funcional, endpoints, contratos publicos ni esquema de base de datos. Use cuando el usuario pida limpiar warnings de Maven o del compilador, normalizar imports o anotaciones, revisar falsos positivos del IDE, o dejar el backend mas limpio manteniendo Java 17 y Spring Boot 3.4.1.
---

# Backend Warning Cleanup

Analiza el backend en `apps/api` y corrige solo problemas menores verificables por compilacion o revision directa del codigo. Prioriza warnings reales del compilador o de Maven sobre alertas del IDE que no afecten el build.

## Alcance

- Limitar el trabajo a `apps/api`.
- Mantener Java `17` y Spring Boot `3.4.1`.
- No cambiar nombres de endpoints, DTOs, entidades, servicios, repositorios ni contratos de API.
- No cambiar logica de negocio salvo que el ajuste sea minimo y necesario para eliminar un warning obvio.
- No cambiar esquema de base de datos.
- Si aparece un warning real por dependencias o versiones, proponer el cambio minimo en `pom.xml` antes de aplicarlo.

## Flujo

1. Revisar `agents.md`, `apps/api/pom.xml` y la estructura actual antes de editar.
2. Ejecutar `./apps/api/mvnw -f apps/api/pom.xml clean compile`.
3. Separar warnings reales del build de falsos positivos del IDE.
4. Corregir solo problemas de bajo riesgo:
   - imports no usados
   - formato inconsistente
   - variables locales innecesarias
   - anotaciones faltantes o redundantes simples
   - warnings menores de compilacion o plugins
5. Evitar refactors amplios, renombres o cambios de flujo.
6. Ejecutar de nuevo `./apps/api/mvnw -f apps/api/pom.xml clean compile`.
7. Ejecutar `./apps/api/mvnw -f apps/api/pom.xml test`.
8. Reportar cambios, warnings corregidos y warnings restantes.

## Criterio Para Warnings

- Tratar como warning real lo que salga de `mvn clean compile`, `mvn test` o inspeccion directa consistente con el codigo.
- Tratar como posible falso positivo del IDE lo que no aparezca en Maven y no represente bug, warning del compilador o riesgo claro.
- Documentar explicitamente cuando algo se deje intacto por ser falso positivo del IDE.

## Reglas De Edicion

- Usar `apply_patch` para cambios manuales.
- No tocar dependencias o versiones ya estabilizadas para compilar, especialmente Java, Maven y Lombok, salvo aprobacion explicita o warning real ineludible.
- Si `mvn test` falla por una causa preexistente no relacionada con los warnings corregidos, no improvisar cambios grandes; dejarlo documentado.
- Preferir cambios pequenos y verificables.

## Entrega

- Entregar:
  - archivos modificados
  - warnings corregidos
  - warnings restantes
  - comandos ejecutados y resultado
- Si no hubo warnings reales del compilador, decirlo explicitamente.

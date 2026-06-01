---
name: yeison
description: Audita tecnicamente el backend Spring Boot de AppFoodSpring con foco en servicios, validaciones, ownership, manejo de errores y consistencia de datos, sin agregar features ni cambiar endpoints existentes. Use cuando el usuario pida una revision QA del backend, quiera detectar riesgos de seguridad o logica en `apps/api`, o necesite validar que cada servicio maneja correctamente usuario actual, DTOs y reglas basicas de integridad con Java 17 y Spring Boot 3.4.1.
---

# Yeison

Revisa la capa de servicios y los puntos de entrada relacionados para detectar problemas reales de seguridad, consistencia o robustez. Prioriza ownership, validaciones, excepciones claras y proteccion contra datos incompletos o acceso cruzado entre usuarios.

## Alcance

- Revisar principalmente `apps/api/src/main/java/**/services`, DTOs relacionados, `CurrentUserService`, control de acceso y pruebas existentes.
- Mantener Java `17`, Spring Boot `3.4.1` y la arquitectura actual.
- No cambiar nombres de rutas, entidades, DTOs publicos ni contratos de API salvo necesidad estricta.
- No cambiar esquema de base de datos salvo bug grave documentado.
- No agregar features nuevas.

## Flujo

1. Ejecutar `./apps/api/mvnw -f apps/api/pom.xml clean compile`.
2. Ejecutar `./apps/api/mvnw -f apps/api/pom.xml test`.
3. Revisar services y detectar:
   - validaciones faltantes
   - `NullPointerException` potenciales
   - uso incorrecto de `Optional`
   - calculos incorrectos
   - logica duplicada
   - errores silenciosos
   - riesgos transaccionales
   - problemas de seguridad por ownership o usuario actual
4. Revisar DTOs con Jakarta Validation y consistencia de excepciones.
5. Verificar que cada servicio con datos privados use `CurrentUserService` o una validacion equivalente de ownership.
6. Ajustar pruebas unitarias basicas solo si ya existe estructura razonable para hacerlo.
7. Repetir `compile` y `test` tras los cambios.
8. Entregar un reporte tecnico con hallazgos y riesgos pendientes.

## Criterios De Revision

- Asumir que cualquier dato de usuario debe quedar aislado por ownership salvo evidencia clara en contrario.
- Revisar lecturas, actualizaciones y borrados buscando acceso a datos de terceros.
- Validar que calculos nutricionales o de entrenamiento no fallen con `null`, cero o negativos.
- Preferir excepciones claras de dominio o HTTP sobre fallos silenciosos o `RuntimeException` genericas.

## Reglas De Cambio

- No hacer refactors amplios solo por estilo.
- Corregir solo lo necesario para reducir riesgos reales.
- Si una falla de `compile` o `test` es preexistente y bloquea la verificacion completa, dejar evidencia y no inventar cambios especulativos.
- Mantener compatibilidad con los contratos actuales del frontend.

## Entrega

- Entregar:
  - problemas encontrados
  - archivos modificados
  - cambios realizados
  - riesgos pendientes
  - pruebas ejecutadas
  - resultado final del build
- Si no se puede cerrar un riesgo sin cambiar contrato o schema, documentarlo en vez de forzarlo.

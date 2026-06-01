# AppFoodSpring Agents

Estado actual:

- La API de Spring Boot vive en `apps/api`.
- El frontend de Next.js vive en `apps/web`.
- Los puertos por defecto persistentes del proyecto son: backend `8081` y frontend `4001`.
- No modificar versiones ni dependencias del backend que ya quedaron estables para compilación: Spring Boot `3.4.1`, Java `17`, Maven Wrapper `3.9.9` y Lombok `1.18.40`.
- Lombok quedó configurado para compilar correctamente como dependencia `provided` y también en `maven-compiler-plugin` mediante `annotationProcessorPaths`; no tocar esa configuración salvo que haya una migración intencional.
- El backend ahora usa autenticación JWT stateless con `SecurityConfig` y `JwtAuthenticationFilter`.
- Únicamente `/auth/register`, `/auth/login`, `/auth/refresh-token`, `OPTIONS /**` y `/actuator/health` son públicas; el resto de endpoints requiere bearer token.
- El backend no usa prefijo global `/api`; las rutas expuestas son directas como `/auth/login`, `/foods`, `/recipes/me`, etc.
- Los endpoints se prueban desde `apps/api/http/*.http`.
- Para probar el backend se debe ejecutar primero `apps/api/http/00-auth.http` para obtener `access_token` y `refresh_token`.
- Los archivos `.http` restantes asumen esas variables globales y requieren reemplazar `REEMPLAZAR_UUID_*` con IDs reales.
- Los módulos principales ya implementados en backend incluyen: `auth`, `users`, `onboarding`, `foods`, `recipes`, `recipe-posts`, `food-logs`, `fridge`, `daily-notes`, `meal-plans`, `exercises`, `workout-plans`, `workout-logs` y `ai`.
- Los contratos del backend viven en `apps/api/src/main/java/com/demoapi/apicomida/dtos`.
- El backend ya no usa SQLite; la base principal ahora debe entrar por PostgreSQL/Supabase usando `SUPABASE_DB_URL`, `SUPABASE_DB_USER` y `SUPABASE_DB_PASSWORD` o sus aliases `SPRING_DATASOURCE_*`.
- La configuración carga variables desde `.env` y `apps/api/.env` mediante `spring.config.import`.
- La verificación mínima de compilación del backend es `./apps/api/mvnw -f apps/api/pom.xml -DskipTests compile`.
- Los tests del backend usan H2 en memoria con compatibilidad PostgreSQL para no depender de una instancia real de Supabase.
- El frontend debe apuntar al backend con `NEXT_PUBLIC_API_URL`, cuyo valor por defecto persistente es `http://localhost:8081`.
- El script `npm run dev --workspace web` arranca Next.js en `http://localhost:4001` y `npm run start --workspace web` conserva ese mismo puerto por defecto.
- Si se ajustan puertos en el futuro, también se debe revisar `CorsConfig`, `README.md`, `apps/api/http/*.http` y cualquier default del cliente frontend.
- Si se construyen pantallas temporales para probar endpoints desde Next, separar la lógica HTTP reutilizable del render de UI y reutilizar JWT en peticiones autenticadas.

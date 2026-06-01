# AppFoodSpring Monorepo

Monorepo para AppFoodSpring con backend en Spring Boot y frontend en Next.js. En esta etapa el backend ya expone autenticacion JWT, perfil, onboarding, alimentos, registros diarios, recetas, publicaciones, planes, refri, ejercicios, entrenamientos, notas e historial IA.

## Estructura

```text
apps/
  api/
    http/  -> archivos .http para probar endpoints
  web/
```

## Requisitos

- Node.js 20+
- npm 10+
- Java 17+

## Instalacion

```bash
npm install
./apps/api/mvnw -f apps/api/pom.xml dependency:go-offline
```

## Comandos principales

```bash
npm run dev
npm run dev:api
npm run dev:web
npm run build
npm run test:api
```

## URLs locales

- Frontend: `http://localhost:4001`
- API: `http://localhost:8081`

## Base de datos local

La API usa PostgreSQL y puede conectarse directo a Supabase mediante JPA/Hibernate.

Variables recomendadas:

- `SUPABASE_DB_URL`
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`

Tambien se aceptan los aliases de Spring:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Ejemplo de URL JDBC para Supabase:

`jdbc:postgresql://<db-host>.supabase.co:5432/postgres?sslmode=require`

## Variables utiles

- `NEXT_PUBLIC_API_URL`
- `SERVER_PORT`
- `SUPABASE_DB_URL`
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_SECURITY_JWT_SECRET`
- `APP_SECURITY_JWT_ACCESS_EXPIRATION_SECONDS`
- `APP_SECURITY_JWT_REFRESH_EXPIRATION_SECONDS`
- `APP_AI_ENABLED`

## Probar endpoints con archivos `.http`

1. Levanta la API con `npm run dev:api`.
2. Abre los archivos de [apps/api/http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http).
3. Ejecuta primero [00-auth.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/00-auth.http:1).
4. Ese archivo guarda `access_token` y `refresh_token` como variables globales del cliente HTTP compatible con IntelliJ/JetBrains.
5. Ejecuta después los demás `.http` por módulo.
6. Sustituye los valores `REEMPLAZAR_UUID_*` por IDs reales que te devuelvan las respuestas previas.

## Orden recomendado de pruebas

1. [00-auth.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/00-auth.http:1)
2. [01-users-onboarding.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/01-users-onboarding.http:1)
3. [02-foods-food-logs.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/02-foods-food-logs.http:1)
4. [03-recipes-posts.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/03-recipes-posts.http:1)
5. [04-meal-plans-fridge.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/04-meal-plans-fridge.http:1)
6. [05-exercises-workout-plans.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/05-exercises-workout-plans.http:1)
7. [06-workout-logs-daily-notes.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/06-workout-logs-daily-notes.http:1)
8. [07-ai.http](/Users/Gil/Documents/PR/SDD/Codex/AppFoodSpring/apps/api/http/07-ai.http:1)

## Cobertura actual del backend

- `Auth`: registro, login, refresh, logout, `me`
- `Users`: perfil propio, patch, baja logica, stats
- `Onboarding`: create, read, patch, calculate-goals
- `Foods`: CRUD, search, barcode, category
- `Food logs`: create, date, range, summary, patch, delete
- `Recipes`: CRUD, public, me, ingredients, calculate-nutrition
- `Recipe posts`: CRUD, like, unlike
- `Meal plans`: CRUD, dias, meals, analyze
- `Fridge`: CRUD, match-recipes
- `Exercises`: CRUD, search, muscle-group
- `Workout plans`: CRUD, dias, exercises
- `Workout logs`: CRUD, date, range, summary, nested exercises
- `Daily notes`: CRUD, date, range
- `AI`: endpoints scaffold con persistencia en historial

## Verificacion

La verificacion automatizada actual del backend se ejecuta con:

```bash
./apps/api/mvnw -f apps/api/pom.xml test
```

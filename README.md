# AppFoodSpring Monorepo

Monorepo para una aplicacion de comida con backend en Spring Boot y frontend en Next.js.

## Estructura

```text
apps/
  api/   -> Spring Boot API
  web/   -> Next.js frontend
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

Correr ambos proyectos al mismo tiempo:

```bash
npm run dev
```

Correr solo la API:

```bash
npm run dev:api
```

Correr solo el frontend:

```bash
npm run dev:web
```

Construir ambos proyectos:

```bash
npm run build
```

Probar backend:

```bash
npm run test:api
```

## URLs locales

- Frontend: `http://localhost:3000`
- API: `http://localhost:8080`

## Flujo de prueba rapido

1. Ejecuta `npm run dev`.
2. Abre `http://localhost:3000`.
3. Verifica que carguen alimentos y recetas.
4. Crea una receta nueva desde el formulario del dashboard.

## Variables utiles

- `NEXT_PUBLIC_API_URL`: cambia la URL base de la API para el frontend.
- `SERVER_PORT`: cambia el puerto del backend.
- `SPRING_DATASOURCE_URL`: permite sustituir SQLite por otra base de datos.
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Base de datos local

Por defecto el backend usa SQLite con JPA/Hibernate y guarda el archivo en:

`apps/api/data/appfoodspring.db`

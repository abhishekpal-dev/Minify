# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Minify is a URL-shortener application with two sub-projects:
- `minify-backend` — Spring Boot 4.1.0 REST API (Java 21)
- `minify-frontend` — Angular 21 SPA

## Backend (`minify-backend`)

### Commands

```bash
# Run
./mvnw spring-boot:run

# Build (skip tests)
./mvnw package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=DemoApplicationTests
```

### Stack & Config

- **Java 21**, Spring Boot 4.1.0, Maven wrapper (`mvnw`)
- **PostgreSQL** — `localhost:5432`, DB `Minify`, user `postgres`, password `secret`
- **Redis** — `localhost:6379` (override via `REDIS_HOST` env var); used as the Spring Cache provider
- **Spring Security** with JWT — secret via `JWT_SECRET` env var (default is a placeholder); tokens expire in 15 minutes
- **Lombok** — used for boilerplate reduction; annotation processing is configured in the Maven compiler plugin
- **Actuator** — `health` and `info` endpoints exposed at `/actuator`
- Server runs on port **8080**; `hibernate.ddl-auto=update` so schema evolves automatically

Base package: `com.minify.surl`

## Frontend (`minify-frontend`)

### Commands

```bash
cd minify-frontend

# Dev server (http://localhost:4200)
npm start

# Production build
npm run build

# Run tests (Vitest)
npm test

# Generate a new component
npx ng generate component <name>
```

### Stack & Conventions

- **Angular 21** with standalone components (no NgModules)
- **TypeScript 5.9** with strict mode
- **SCSS** for all styles (configured as the default in `angular.json`)
- **Vitest** for unit tests (not Karma/Jest)
- **Prettier** for formatting (config in `.prettierrc`)
- Component files use `.html` / `.scss` / `.ts` (no `.component.*` infix) — follow the existing `app.html` / `app.scss` / `app.ts` naming pattern

## Infrastructure Prerequisites

Both services must be running locally before starting the backend:

| Service    | Default address      |
|------------|----------------------|
| PostgreSQL | `localhost:5432`     |
| Redis      | `localhost:6379`     |

Environment variables that override defaults: `REDIS_HOST`, `JWT_SECRET`.

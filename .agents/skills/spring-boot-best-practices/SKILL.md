---
name: spring-boot-best-practices
description: Generate or modify Spring Boot API and Spring Web monolith projects using Maven, Java 25, jar packaging, application.properties, layered packages, REST controllers, services, JpaRepository repositories, models, DTO records, explicit mappers, and optional Thymeleaf layouts. Use when the user asks to create a basic Spring Boot API, create a Spring Web monolith, or create, add, or modify a Spring entity, repository, service, controller, DTO, or mapper.
---

# Spring Boot Best Practices

## Overview

Use this skill to scaffold or extend a Spring Boot project with a consistent repository/service/controller architecture. Prefer current stable Spring Boot 4.x from spring.io, with `4.0.6` or newer stable when available, and never below `4.0.3`.

## Project Defaults

- Use Java `25`.
- Use Spring Boot `4.0.6` or newer stable from spring.io when available; if a newer stable exists, use it.
- Use Maven, jar packaging, and Maven Wrapper files: `.mvn/`, `mvnw`, and `mvnw.cmd`.
- Use `application.properties`, not YAML.
- Use Maven `groupId` `com.andres.course.codex.springboot`.
- Set Maven `artifactId` to the current workspace directory name unless the user explicitly gives a different project name.
- Use base package `com.andres.course.codex.springboot.{artifactId}.app`; normalize `artifactId` into a valid Java package segment when needed.
- Include these baseline dependencies:
  - `spring-boot-starter-web`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-data-jpa`
  - `com.h2database:h2`
  - `spring-boot-devtools`
  - `spring-boot-starter-actuator`
  - `spring-boot-starter-test` for tests
- For monolith Spring Web pages, add `spring-boot-starter-thymeleaf`.

## Package Structure

Create packages under the base package:

```text
src/main/java/{basePackage}/
  Application.java
  controllers/
  dtos/
  mappers/
  models/
  repositories/
  services/
```

Use plural package names exactly as shown for `models`, `controllers`, `repositories`, and `services`.

## Layer Rules

- `controllers`: expose only REST endpoints, validate request input with Bean Validation, delegate to services, and return DTOs.
- `services`: contain business logic, coordinate repositories, handle transactions when needed, and convert between entities and DTOs through mappers.
- `repositories`: define interfaces that extend `JpaRepository<Entity, IdType>`.
- `models`: define JPA entities only; do not expose entities directly from REST controllers.
- `mappers`: define explicit mapper classes that convert `entity -> dto` and `dto -> entity`.
- `dtos`: use Java `record`, not class.

## DTO Rules

Use the same DTO record shape for request and response unless the user asks for separate DTOs. Exclude sensitive or audit fields from DTOs, including:

```text
password
created_at
updated_at
create_at
update_at
createdAt
updatedAt
createAt
updateAt
```

Use validation annotations on DTO record components for inbound data, for example `@NotBlank`, `@NotNull`, `@Email`, `@Size`, and `@Positive`.

## Web Monolith Rules

When building a Spring Web monolith with Thymeleaf:

- Use Thymeleaf templates and Tailwind CSS.
- Use reusable layouts and fragments.
- Include semantic HTML with `header`, `main`, and `footer`.
- Keep the main content centered.
- Prefer this view structure:

```text
src/main/resources/templates/
  layouts/base.html
  fragments/header.html
  fragments/footer.html
```

## Maven Commands

Prefer Maven Wrapper commands for local work:

```bash
./mvnw -DskipTests spring-boot:run
./mvnw test
./mvnw package
```

If the wrapper is missing in an existing project, add or regenerate it before documenting commands that rely on Maven.

## Implementation Checklist

- Inspect the existing project before editing; follow current naming and style where compatible with this skill.
- For new projects, create `pom.xml`, Maven Wrapper files, `src/main/java`, `src/main/resources/application.properties`, and `src/test/java`.
- Configure H2 and JPA defaults in `application.properties` for local development.
- Keep controllers thin and services responsible for application behavior.
- Add focused tests for service or controller behavior when adding non-trivial logic.

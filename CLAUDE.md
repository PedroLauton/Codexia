# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build & Run
```bash
# Build all modules
./gradlew build

# Run the application (entry point is codexia-main)
./gradlew :codexia-main:bootRun

# Build without running tests
./gradlew build -x test
```

### Tests
```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :codexia-snippet:test

# Run a single test class
./gradlew :codexia-snippet:test --tests "br.com.codexia.snippet.application.usecase.CreateSnippetUseCaseImplTest"

# Run a single test method
./gradlew :codexia-snippet:test --tests "br.com.codexia.snippet.application.usecase.CreateSnippetUseCaseImplTest.WhenAllInputsAreValid.shouldCreateSnippetWithoutTags"
```

### Database
```bash
# Start PostgreSQL (dev on port 5432, test on port 5433)
docker-compose up -d

# Stop containers
docker-compose down
```

## Architecture

Codexia is a multi-module Gradle project following **Clean Architecture** with **CQRS** (Command/Query segregation at the port level).

### Module layout
- **`codexia-shared`** — Cross-cutting domain primitives: `WorkspaceId`, `AccountId`, base exceptions (`DomainException`, `ResourceNotFoundException`, `ErrorCode`). Every other module depends on this.
- **`codexia-snippet`** — Core business module (most active). Contains the full CA stack for snippets, categories, and tags.
- **`codexia-main`** — Spring Boot application entry point. Aggregates all modules, owns Flyway migrations (`src/main/resources/db/migration/`), and holds `application.properties`.
- **`codexia-ai`**, **`codexia-identity`**, **`codexia-notification`**, **`codexia-workspace`** — Planned/stub modules for AI integration, auth, notifications, and workspace management.

### Layers inside `codexia-snippet`
```
domain/
  model/          ← Pure domain entities (Snippet, Category, Tag, SnippetVersion) and value objects (*Id types)
  exception/      ← Domain-specific exceptions extending DomainException

application/
  ports/
    input/        ← Use case interfaces (one per operation, e.g. CreateSnippetUseCase)
    output/
      command/    ← Write port interfaces (e.g. SnippetCommandPort)
      query/      ← Read port interfaces (e.g. SnippetQueryPort, CategoryQueryPort)
  usecase/        ← Implementations of input ports; injected via Spring @Configuration beans
  dto/
    command/      ← Input records for use cases
    response/     ← Output records from use cases
  usecase/mapper/ ← Static mappers from domain → response DTOs

infrastructure/
  adapters/
    input/rest/   ← Spring @RestController + request DTOs
    output/persistence/
      adapter/    ← Implements command/query ports using JPA repositories
      entity/     ← JPA @Entity classes
      mapper/     ← Maps domain ↔ JPA entity
      repository/ ← Spring Data JPA interfaces
  config/         ← @Configuration classes that wire use case implementations as Spring beans
```

### Key design decisions

**Use case wiring:** Use cases are plain Java classes with no `@Service` or `@Component`. They use constructor injection only and are instantiated in `infrastructure/config/*UseCaseConfig.java` as Spring `@Bean`s. Never add Spring stereotypes to classes in `application/usecase/`.

**CQRS ports:** Command ports (`*CommandPort`) handle writes; query ports (`*QueryPort`) handle reads. Adapters implement these interfaces in `infrastructure/adapters/output/persistence/adapter/`.

**Soft delete pattern:** Domain entities (`Snippet`, `Category`, `Tag`) use a `deletedAt` timestamp. Deleted entities throw `Deleted*MutationException` on any state-modifying method. `restore()` nulls out `deletedAt`. `purge` operations perform hard deletes.

**Versioning:** Snippets are never edited in place. Each update creates a new `SnippetVersion` appended to the `versions` set.

**Workspace isolation (multitenancy):** Every entity carries a `WorkspaceId`. The `workspaceId` is always extracted from the URL path parameter — never from JWT claims or any other source. Every query port method must accept and scope by `WorkspaceId`.

**Pagination:** `Page<T>` and `Pageable` (Spring Data types) are forbidden in use cases and ports. Use `PageQuery` (input) and `PageResult<T>` (output) at the application boundary. Conversion to Spring's `PageRequest` happens only inside persistence adapters.

**Domain events:** Never publish events to a message broker inside a `@Transactional` method (Dual Write problem). All event publishing must go through the Transactional Outbox pattern.

### Use Case Orchestration Pattern

`execute()` methods must read as a high-level orchestration flow. Inline conditionals,
null checks, and validation logic inside `execute()` are not allowed.

Rules:
- `execute()` tells WHAT happens, not HOW
- Private methods carry the detail and are named after their intent
- Each private method has a single responsibility
- Duplicated calls (save, publish, map) must appear once — never inside if/else branches

Example structure:
```
  execute()
    → parse primitives from command
    → find aggregate via finder
    → resolve parameters via private methods
    → call domain method (aggregate mutation)
    → call output ports (save, events)
    → return response via mapper

  private resolveX()     → derives a value, may throw
  private validateX()    → guards only, always void, throws on violation
  private buildX()       → constructs a value object or entity without side effects
```

Any use case that does not follow this pattern is considered an architectural violation.

### Dependency rules
- `domain` imports only `java.*` — no Spring, no JPA, no application or infrastructure types.
- `application` imports `domain` and `java.*` only — never imports anything from `infrastructure`.
- `@Transactional` is allowed only in infrastructure adapters (`infrastructure/adapters/output/persistence/adapter/`), never in use cases or domain classes.

### Database
- Flyway migrations live in `codexia-main/src/main/resources/db/migration/`.
- Dev DB: `localhost:5432/codexia` | Test DB: `localhost:5433/codexia_test` (via `application-test.properties`).
- Schema is validated by Hibernate (`ddl-auto=validate`); all DDL changes go through Flyway migrations.

### Testing
- Unit tests use **Mockito** (`@ExtendWith(MockitoExtension.class)`) with `@Nested` + `@DisplayName` for BDD-style grouping.
- **ArchUnit** (`archunit-junit5`) is available in `codexia-snippet` for enforcing architectural rules.
- Tests target use case implementations directly, mocking ports — no Spring context loaded.

---
description: Architectural rules for crm-engine development
globs: ["src/**/*.kt"]
---

# Architecture Rules

## Hexagonal Architecture Boundaries
- Domain layer (`domain/`) must NEVER depend on infrastructure or application layers
- Application layer (`application/`) may depend on domain but NEVER on infrastructure
- Infrastructure layer (`infrastructure/`) implements domain ports (repository interfaces, adapters)
- API layer (`api/`) handles HTTP concerns only — delegates to CommandBus immediately

## CQRS
- Commands modify state and go through `CommandBus` → `ICommandHandler`
- Queries read state and go through `QueryBus` → `IQueryHandler`
- Never mix reads and writes in the same handler

## Use Cases
- Each use case is a single `operator fun invoke(...)` returning `Result<Unit>`
- Use cases must publish domain events via `IEventAdapter` after persistence
- Use cases are registered as `factory` in Koin (not `single`)

## Domain Events
- Define event data classes in `application/event/` under the appropriate aggregate package
- Every event must implement its aggregate's sealed interface (e.g., `IBonusEvent`, `ISpinEvent`)
- Add routing key mapping in `infrastructure/rabbitmq/mapper/EventMapper.kt`

## Repositories
- Domain ports: `I<Entity>Repository` in `domain/<aggregate>/repository/`
- ClickHouse implementations: `ClickHouse<Entity>Repository` in `infrastructure/clickhouse/repository/`
- PostgreSQL implementations: `Exposed<Entity>Repository` in `infrastructure/exposed/repository/`

## Dependency Injection
- All wiring in single `infrastructureModule` in `infrastructure/koin.kt`
- Multi-binding pattern: `bind Interface::class` + `getAll()` for CommandBus, QueryBus, and ConditionRuleEvaluatorResolver
- Infrastructure clients and repositories are `single` scoped; use cases are `factory` scoped

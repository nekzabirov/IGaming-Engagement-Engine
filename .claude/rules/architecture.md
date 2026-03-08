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
- Define event data classes in `domain/event/` under the appropriate aggregate package
- Every event must implement its aggregate's sealed interface (e.g., `IBonusEvent`, `ISpinEvent`)
- Add routing key mapping in `infrastructure/rabbitmq/mapper/EventMapper.kt`

## Repositories
- Domain ports: `I<Entity>Repository` in `domain/<aggregate>/repository/`
- ClickHouse implementations: `ClickHouse<Entity>Repository` in `infrastructure/clickhouse/repository/`
- PostgreSQL implementations: `Exposed<Entity>Repository` in `infrastructure/exposed/repository/`

## Journey Node Processing
- `IJourneyNode` is an abstract class (not interface) with `id: Long = Long.MIN_VALUE` and `next` as constructor parameters, plus built-in circular dependency validation in `init`
- `ITriggerJourneyNode` is an abstract class extending `IJourneyNode` — subclass data classes pass `_id` and `_next` as private constructor params forwarded to super
- `JourneyNodeProcessResolver` resolves and delegates to `JourneyNodeProcess<N>` implementations via `nodeType: KClass` matching
- `JourneyNodeProcess.process()` returns `JourneyNodeProcess.Response?` (contains `nextNode` + `output` map) — `null` means no match
- `Journey` exposes a `tail` property that traverses the `next` chain from `head` to return the last node

## Dependency Injection
- All wiring in single `infrastructureModule` in `infrastructure/koin.kt`
- Multi-binding pattern: `bind Interface::class` + `getAll()` for CommandBus, QueryBus, and ConditionRuleEvaluatorResolver
- Infrastructure clients and repositories are `single` scoped; use cases are `factory` scoped

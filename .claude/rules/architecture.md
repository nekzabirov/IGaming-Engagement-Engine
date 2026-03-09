---
description: Architectural rules for crm-engine development
globs: ["src/**/*.kt"]
---

# Architecture Rules

## Hexagonal Architecture Boundaries
- Domain layer (`domain/`) must NEVER depend on infrastructure or application layers
- Application layer (`application/`) may depend on domain but NEVER on infrastructure
- Infrastructure layer (`infrastructure/`) implements domain ports (repository interfaces, adapters)
- API layer (`api/`) handles HTTP concerns and contains command handlers with business logic

## CQRS
- Commands modify state and go through `CommandBus` → `ICommandHandler`
- Queries read state and go through `QueryBus` → `IQueryHandler`
- Never mix reads and writes in the same handler

## Command Handlers
- Command handlers contain the full business logic (no separate use case layer)
- Command handlers persist to ClickHouse and publish domain events via `IEventAdapter`
- Command handlers check for existing records before creating (idempotency)

## Domain Events
- Define event data classes in `application/event/` under the appropriate aggregate package
- Every event must implement its aggregate's sealed interface (e.g., `IBonusEvent`, `ISpinEvent`)
- Add routing key mapping in `infrastructure/external/rabbitmq/mapper/EventMapper.kt`

## Repositories
- Domain ports: `I<Entity>Repository` in `domain/repository/player/`
- Journey ports: `IJourneyRepository`, `IJourneyInstantRepository` in `domain/repository/`
- ClickHouse implementations: `ClickHouse<Entity>Repository` in `infrastructure/database/clickhouse/repository/`
- PostgreSQL implementations: `Exposed<Entity>Repository` in `infrastructure/database/exposed/repository/`

## Journey Node Processing
- `IJourneyNode` is an abstract class (not interface) with `open val id: Long = Long.MIN_VALUE` and `open val next` as constructor parameters, plus built-in circular dependency validation in `init`
- `IJourneyNode` does **not** define `inputParams`/`outputParams` — param knowledge is externalized to `JourneyNodeNomenclature<N>` strategy implementations
- `ITriggerJourneyNode` is an abstract class extending `IJourneyNode` with `override val id` and `override val next` — subclass data classes use `override val` for these params directly
- `PlayerJourneyNode` evaluates `IPlayerDefinition` rules with `matchNode`/`notMatchNode` branching
- `JourneyNodeProcess.process()` returns `JourneyNodeProcess.Response?` (contains `nextNode` + `output` map) — `null` means no match
- `JourneyNodeNomenclature<N>` strategy interface (in `domain/strategy/`) declares `inputParams(node)` and `outputParams(node)` per node type, with implementations in `infrastructure/journey/` alongside each node class
- `Journey` exposes a `tail` property that traverses the `next` chain from `head` to return the last node
- `JourneyInstant` tracks player progress through a journey (current node + payload)

## Dependency Injection
- All wiring in single `infrastructureModule` in `infrastructure/koin.kt`
- Multi-binding pattern: `bind Interface::class` + `getAll()` for CommandBus, QueryBus, JourneyNodeNomenclature, and player definition evaluators
- All bindings are `single` scoped

---
description: Architectural rules for crm-engine development
globs: ["src/**/*.kt"]
---

# Architecture Rules

## Hexagonal Architecture Boundaries
- Domain layer (`domain/`) must NEVER depend on infrastructure or application layers
- Application layer (`application/`) may depend on domain but NEVER on infrastructure
- Infrastructure layer (`infrastructure/`) implements domain ports (repository interfaces, adapters)
- API layer (`api/`) handles HTTP concerns and dispatches to command handlers

## CQRS
- Commands modify state and go through `CommandBus` → `ICommandHandler`
- Queries read state and go through `QueryBus` → `IQueryHandler`
- Never mix reads and writes in the same handler

## Command Handlers & Use Cases
- Command handlers in `api/command/` dispatch to use cases for business logic
- Use cases in `application/usecase/player/` encapsulate per-aggregate operations
- Each use case takes a `Command` or `Details` inner data class, returns `Result<Unit>` via `runCatching`
- Use cases depend on domain repository ports and `IEventAdapter`/`ICurrencyAdapter`
- Command handlers persist to ClickHouse and publish domain events via use cases
- Idempotency: check for existing records before creating

## Use Case Layer
- 13 use cases organized by aggregate: `bonus/` (Issue, Lost, StartWagering, Wagered), `freespin/` (Issue, Activate, Cancel, Finish), `invoice/` (Create, Update), `spin/` (Place, Settle), `player/` (Update)
- Use cases use `operator fun invoke()` for clean call syntax
- Domain imports: `com.nekgambling.domain.model.player.*` for models, `com.nekgambling.domain.repository.player.*` for repositories, `com.nekgambling.domain.vo.*` for value objects

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
- Each trigger node has a `companion object { const val TRIGGER_NAME = "..." }` (e.g., `"bonus"`, `"freespin"`, `"invoice"`, `"segment"`) and its process checks `payload["triggerName"]` against `TRIGGER_NAME`, returning `null` (no match) if missing or mismatched
- `PlayerJourneyNode` evaluates `IPlayerDefinition` rules with `matchNode`/`notMatchNode` branching
- `JourneyNodeProcess.process()` returns `JourneyNodeProcess.Response?` (contains `nextNode` + `output` map) — `null` means no match
- `JourneyNodeNomenclature<N>` strategy interface (in `domain/strategy/`) declares `identity`, `inputParams(node)` and `outputParams(node)` per node type, with implementations in `infrastructure/journey/` alongside each node class. `identity` is a camelCase identifier without the "JourneyNode" prefix (e.g., `"bonusTrigger"`, `"pushAction"`, `"playerProfileExtractor"`)
- `Journey` exposes a `tail` property that traverses the `next` chain from `head` to return the last node
- `IActionJourneyNode` is an abstract class extending `IJourneyNode` for side-effect action nodes
- `IPushActionJourneyNode` is a sealed class extending `IActionJourneyNode` with `templateId` and `placeHolders` — concrete subtypes: `EMailPushActionJourneyNode`, `SmsPushActionJourneyNode`, `InAppPushActionJourneyNode`
- `IssueBonusActionJourneyNode` is a sealed class extending `IActionJourneyNode` — concrete subtypes: `IssueFixedBonusActionJourneyNode` (currency + amount), `IssueDynamicBonusActionJourneyNode`
- `IActionJourneyNodeProcess<T>` is the abstract base for action node processors, extending `JourneyNodeProcess<T>`; `IPushActionJourneyNodeProcess` processes all push subtypes via sealed class matching
- `JourneyInstant` tracks player progress through a journey (current node + payload)

## Condition Journey Nodes
- `IConditionJourneyNode` is an abstract class extending `IJourneyNode` with `inputKey: String`, `matchNode: IJourneyNode?`, `notMatchNode: IJourneyNode?`, and abstract `evaluate(value: String): Boolean`
- The `next` chain follows `matchNode` (passed to `IJourneyNode` super constructor)
- `ConditionJourneyNodeProcess` validates that `inputKey` exists in the payload (throws if missing), calls `evaluate()`, and returns `matchNode` on true or `notMatchNode` on false with empty output
- `ConditionJourneyNodeNomenclature` declares empty `inputParams()` and `outputParams()`
- Concrete condition nodes (e.g., `NumberConditionNode`) extend `IConditionJourneyNode` as sealed classes

## Extractor Journey Nodes
- `IExtractorJourneyNode` is an abstract class extending `IJourneyNode` with a `suspend fun extract(playerId: String, inputs: Map<String, Any>): Map<String, Any>` method
- `PlayerProfileExtractor` extracts all player fields with `player:` prefix (e.g., `player:username`, `player:email`)
- `PercentageAmountExtractor` (extends `IAmountExtractor`) calculates amounts with percentage + optional max cap
- Each extractor has its own `JourneyNodeNomenclature` implementation registered in Koin

## Dependency Injection
- All wiring in single `infrastructureModule` in `infrastructure/koin.kt`
- Multi-binding pattern: `bind Interface::class` + `getAll()` for CommandBus, QueryBus, JourneyNodeNomenclature, JourneyNodeProcess, and player definition evaluators
- All bindings are `single` scoped

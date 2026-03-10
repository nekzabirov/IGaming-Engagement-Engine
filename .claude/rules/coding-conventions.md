---
description: Coding conventions for crm-engine development
globs: ["src/**/*.kt"]
---

# Coding Conventions

## Naming
- Commands: `Process<Entity>Command`
- Command handlers: `Process<Entity>CommandHandler`
- Use cases: `<Verb><Entity>UseCase` (e.g., `IssueBonusUseCase`, `UpdatePlayerUseCase`, `PlaceSpinUseCase`). Journey use cases in `application/usecase/journey/` (e.g., `ProcessJourneyNodeUsecase`)
- Domain repositories: `I<Entity>Repository`
- ClickHouse repos: `ClickHouse<Entity>Repository`
- Exposed repos: `Exposed<Entity>Repository`
- Player definitions (DB only, legacy): `<Name>PlayerDefinition` with `@SerialName("<camelCase>")` — used only for PostgreSQL JSONB serialization
- Player journey nodes: `<Name>PlayerJourneyNode` extending `PlayerJourneyNode` (abstract class), with `override val id`, `override val matchNode`, `override val notMatchNode` constructor params. Each holds its own condition params directly (e.g., `PlayerAgePlayerJourneyNode` has `value: NumberParamValue`)
- Player journey node processors: `<Name>PlayerJourneyNodeProcess` implementing `IPlayerJourneyNodeProcess<T>`, placed in same package as their node class. Returns `Response(nextNode = matchNode/notMatchNode)` based on condition evaluation
- Trigger journey nodes: `<Name>TriggerJourneyNode` extending `ITriggerJourneyNode` (abstract class), with `override val id` and `override val next` constructor params, and a `companion object { const val TRIGGER_NAME = "<name>" }` for trigger name matching
- Journey node processors: `override val nodeType: KClass<N>` (not `node`) to avoid confusion with the `process(node)` parameter
- Journey node nomenclatures: `<NodeType>Nomenclature` objects (e.g., `BonusTriggerJourneyNodeNomenclature`, `PlayerAgePlayerJourneyNodeNomenclature`), placed alongside their node class, registered in Koin with `bind JourneyNodeNomenclature::class`. Each concrete player journey node type has its own nomenclature
- Player journey branching: `matchNode` / `notMatchNode` on `PlayerJourneyNode`
- Action journey nodes: `<Name>ActionJourneyNode` extending `IActionJourneyNode` (abstract class), with `id` and `next` constructor params
- Push action journey nodes: `<Channel>PushActionJourneyNode` extending `IPushActionJourneyNode` (sealed class with `templateId` + `placeHolders`), e.g., `EMailPushActionJourneyNode`, `SmsPushActionJourneyNode`, `InAppPushActionJourneyNode`
- Action node processors: `<Name>ActionJourneyNodeProcess` extending `ActionJourneyNodeProcess<T>`, placed in same package as their node class
- Issue action journey nodes: `Issue<Entity>ActionJourneyNode` in `infrastructure/journey/action/issue/<entity>/` package (e.g., `IssueFreespinActionJourneyNode`)
- Extractor journey nodes: `<Name>Extractor` extending `IExtractorJourneyNode`, in `infrastructure/journey/extractor/<name>/` package
- Trigger node output keys: Use `domain:field` colon-separated prefix format (e.g., `bonus:id`, `invoice:amount`, `freespin:currency`). Input payload keys from upstream events remain camelCase (e.g., `bonusId`, `invoiceAmount`)
- Trigger node input params: All trigger nomenclatures must include `"triggerName"` in `inputParams()`. Process implementations must check `payload["triggerName"]` matches the node's `TRIGGER_NAME` constant at the top, returning `null` if missing or mismatched

## Package Imports
- Domain models: `com.nekgambling.domain.model.player.*` (NOT `domain.player.model`)
- Domain repositories: `com.nekgambling.domain.repository.player.*` (NOT `domain.player.repository`)
- Value objects: `com.nekgambling.domain.vo.*` (Currency, Country, Locale)
- Shared params: `com.nekgambling.domain.shared.param.*`
- Journey domain: `com.nekgambling.domain.model.journey.*`
- Strategy interfaces: `com.nekgambling.domain.strategy.*`

## Monetary Values
- Always use `Long` in minor units (cents) — never `Double` or `BigDecimal` for storage
- Use `ICurrencyAdapter.convertToUnits()` when converting from external decimal amounts

## ClickHouse
- Use `ReplacingMergeTree` with `_version` column for upsertable tables
- Use `SummingMergeTree` for pre-aggregated totals
- Always use `SELECT ... FINAL` when reading from ReplacingMergeTree tables
- Raw JDBC via `ClickHouseClient` — no ORM for ClickHouse

## PostgreSQL
- Use Exposed ORM for all PostgreSQL access
- Tables defined in `infrastructure/database/exposed/table/`
- Repositories in `infrastructure/database/exposed/repository/`
- Journey persistence: `JourneyNodesTable` (single-table inheritance with `type` discriminator column, nullable params per node type, self-references for `next`/`onMismatch`) and `JourneysTable` (references head node)
- `NumberParamValue` JSONB columns use polymorphic serialization via `numberParamValueJson` in `NumberParamValueJson.kt`
- `IPlayerDefinition` JSONB columns use polymorphic serialization via `conditionRuleJson` in `PlayerDefinitionJson.kt`

## Serialization
- Use `kotlinx.serialization` — not Jackson or Gson
- Player definitions use polymorphic serialization registered in `PlayerDefinitionJson.kt`

## Outbound Adapters (Infrastructure)
- Every outbound adapter (repository impl, external client, message broker, etc.) must have its own `<Name>Config` data class in its config/ subpackage
- Config classes read environment variables and are passed into the adapter via constructor injection
- Port interfaces in `application/adapter/` must never read env vars directly — config is always injected from infrastructure
- Register config as `single` in Koin, inject into the adapter: `single { SomeConfig(env("VAR")) }` → `single { SomeAdapter(get()) }`

## Concurrency
- Use Redis distributed locks via `ILockAdapter.withLock()` for operations that must be atomic per player+entity
- ClickHouse operations dispatch on `Dispatchers.IO`

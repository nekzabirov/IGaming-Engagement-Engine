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
- Trigger journey nodes: `<Name>TriggerJourneyNode` extending `ITriggerJourneyNode` (abstract class), with `override val id` and `override val next` constructor params, and a `companion object { const val TRIGGER_NAME = "<name>" }` for trigger name matching
- Journey node processors: `override val nodeType: KClass<N>` (not `node`) to avoid confusion with the `process(node)` parameter
- Journey node nomenclatures: `<NodeType>Nomenclature` objects (e.g., `BonusTriggerJourneyNodeNomenclature`, `PlayerAgeExtractorNomenclature`), placed alongside their node class, registered in Koin with `bind JourneyNodeNomenclature::class`
- Action journey nodes: `<Name>ActionJourneyNode` extending `IActionJourneyNode` (abstract class), with `id` and `next` constructor params
- Push action journey nodes: `<Channel>PushActionJourneyNode` extending `IPushActionJourneyNode` (sealed class with `templateId` + `placeHolders`), e.g., `EMailPushActionJourneyNode`, `SmsPushActionJourneyNode`, `InAppPushActionJourneyNode`
- Action node processors: `<Name>ActionJourneyNodeProcess` extending `ActionJourneyNodeProcess<T>`, placed in same package as their node class
- Issue action journey nodes: `Issue<Entity>ActionJourneyNode` in `infrastructure/journey/action/issue/<entity>/` package (e.g., `IssueFreespinActionJourneyNode`)
- Extractor journey nodes: `<Name>Extractor` extending `IExtractorJourneyNode`, in `infrastructure/journey/extractor/<name>/` package
- Player extractor journey nodes: `<Name>Extractor` extending `IPlayerExtractorJourneyNode` (abstract class with `player:` prefix helper), in `infrastructure/journey/extractor/player/<name>/` package. Use `buildOutput()` helper to auto-prefix keys with `player:`. Nodes: `PlayerProfileExtractor`, `PlayerAgeExtractor`, `SpinTotalExtractor`, `InvoiceTotalExtractor`, `PlayerGgrExtractor`
- Extractor processing: `ExtractorJourneyNodeProcess<N>` is an abstract base class with `abstract extract()`. Each extractor type has its own concrete process class (e.g., `PlayerAgeExtractorProcess`, `AmountExtractorProcess`) that holds dependencies (repos, query bus) — nodes are pure data, no dependencies injected into nodes
- Condition journey nodes: `<Name>ConditionNode` extending `IConditionJourneyNode` (abstract class with `inputKey`, `matchNode`, `notMatchNode`), in `infrastructure/journey/condition/` package. Concrete subtypes grouped as sealed classes (e.g., `NumberConditionNode`). Process: `ConditionJourneyNodeProcess`, Nomenclature: `ConditionJourneyNodeNomenclature` (empty input/output params)
- Trigger node output keys: Use `domain:field` colon-separated prefix format (e.g., `bonus:id`, `invoice:amount`, `freespin:currency`). Input payload keys from upstream events remain camelCase (e.g., `bonusId`, `invoiceAmount`)
- Trigger node input params: All trigger nomenclatures must include `"triggerName"` in `inputParams()`. Process implementations must check `payload["triggerName"]` matches the node's `TRIGGER_NAME` constant at the top, returning `null` if missing or mismatched

## Package Imports
- Domain models: `com.nekgambling.domain.model.player.*` (NOT `domain.player.model`)
- Domain repositories: `com.nekgambling.domain.repository.player.*` (NOT `domain.player.repository`)
- Value objects: `com.nekgambling.domain.vo.*` (Currency, Country, Locale, Payload, Period)
- Shared params: `com.nekgambling.domain.shared.param.*`
- Journey domain: `com.nekgambling.domain.model.journey.*`
- Strategy interfaces: `com.nekgambling.domain.strategy.*`

## Type Aliases
- `Payload` = `Map<String, Any>` (`domain.vo.Payload`) — use for all journey node payload/output parameters instead of raw `Map<String, Any>`
- `Period` = `Pair<Instant, Instant>` (`domain.vo.Period`) — date range for queries

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
- Journey persistence: `JourneyNodesTable` (minimal: `id`, `type`, `journeyId`, `next`) and `JourneysTable` (references head node). Node-specific columns and domain mapping are pending rebuild — repositories are currently stubbed with `TODO`

## Serialization
- Use `kotlinx.serialization` — not Jackson or Gson

## Outbound Adapters (Infrastructure)
- Every outbound adapter (repository impl, external client, message broker, etc.) must have its own `<Name>Config` data class in its config/ subpackage
- Config classes read environment variables and are passed into the adapter via constructor injection
- Port interfaces in `application/adapter/` must never read env vars directly — config is always injected from infrastructure
- Register config as `single` in Koin, inject into the adapter: `single { SomeConfig(env("VAR")) }` → `single { SomeAdapter(get()) }`

## Concurrency
- Use Redis distributed locks via `ILockAdapter.withLock()` for operations that must be atomic per player+entity
- ClickHouse operations dispatch on `Dispatchers.IO`

---
description: Kotlin coding conventions for crm-engine
globs: ["src/**/*.kt"]
---

# Coding Conventions

## Naming
- Commands: `Process<Entity>Command`
- Command handlers: `Process<Entity>CommandHandler`
- Use cases: `<Verb><Entity>UseCase` (e.g., `IssueBonusUseCase`, `PlaceSpinUseCase`)
- Domain repositories: `I<Entity>Repository`
- ClickHouse repos: `ClickHouse<Entity>Repository`
- Exposed repos: `Exposed<Entity>Repository`
- Condition rules: `<Name>ConditionRule` with `@SerialName("<camelCase>")`
- Condition evaluators: `<Name>ConditionRuleEvaluator`
- Trigger rules: `<Name>TriggerRule` with `@SerialName("<camelCase>")`
- Trigger journey nodes: `<Name>TriggerJourneyNode` implementing `ITriggerJourneyNode`
- Journey node processors: `override val nodeType: KClass<N>` (not `node`) to avoid confusion with the `process(node)` parameter
- Journey node params: `inputParams()` for required input keys, `outputParams()` for produced output keys
- Condition branching: `onMatch` / `onMismatch` on `ConditionJourneyNode`
- Journey payload keys: Always prefix with entity name to avoid collisions (e.g., `bonusId`, `bonusStatus`, `freespinId`, `invoiceType`, `invoiceAmount`)

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
- Tables defined in `infrastructure/exposed/table/`
- Repositories in `infrastructure/exposed/repository/`

## Serialization
- Use `kotlinx.serialization` — not Jackson or Gson
- Condition rules use polymorphic serialization registered in `ConditionRuleJson.kt`
- Trigger rules use polymorphic serialization registered in `TriggerRuleJson.kt`

## Outbound Adapters (Infrastructure)
- Every outbound adapter (repository impl, external client, message broker, etc.) must have its own `<Name>Config` data class in `infrastructure/<adapter>/config/`
- Config classes read environment variables and are passed into the adapter via constructor injection
- Port interfaces in `application/adapter/` must never read env vars directly — config is always injected from infrastructure
- Register config as `single` in Koin, inject into the adapter: `single { SomeConfig(env("VAR")) }` → `single { SomeAdapter(get()) }`

## Concurrency
- Use Redis distributed locks via `ILockAdapter.withLock()` for operations that must be atomic per player+entity
- ClickHouse operations dispatch on `Dispatchers.IO`

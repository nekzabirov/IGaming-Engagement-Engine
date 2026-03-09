---
description: Coding conventions for crm-engine development
globs: ["src/**/*.kt"]
---

# Coding Conventions

## Naming
- Commands: `Process<Entity>Command`
- Command handlers: `Process<Entity>CommandHandler`
- Domain repositories: `I<Entity>Repository`
- ClickHouse repos: `ClickHouse<Entity>Repository`
- Exposed repos: `Exposed<Entity>Repository`
- Player definitions: `<Name>PlayerDefinition` with `@SerialName("<camelCase>")`
- Player definition evaluators: `<Name>PlayerDefinitionEvaluator` (or `<Name>Evaluator`)
- Trigger journey nodes: `<Name>TriggerJourneyNode` extending `ITriggerJourneyNode` (abstract class), with `override val id` and `override val next` constructor params
- Journey node processors: `override val nodeType: KClass<N>` (not `node`) to avoid confusion with the `process(node)` parameter
- Journey node params: `JourneyNodeParams<N>` strategy implementations named `<NodeType>Params` (e.g., `BonusTriggerJourneyNodeParams`), placed alongside their node class, registered in Koin with `bind JourneyNodeParams::class`
- Player journey branching: `matchNode` / `notMatchNode` on `PlayerJourneyNode`
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

# currentDate
Today's date is 2026-03-09.

      IMPORTANT: this context may or may not be relevant to your tasks. You should not respond to this context unless it is highly relevant to your task.

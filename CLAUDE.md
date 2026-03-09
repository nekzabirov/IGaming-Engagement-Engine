# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**crm-engine** is a Kotlin/Ktor CRM data processing service for the IGambling platform. It ingests player lifecycle events (registrations, deposits, withdrawals, casino bets) via a single HTTP endpoint, stores analytics data in ClickHouse, manages player journey orchestration via PostgreSQL, and publishes domain events to RabbitMQ. It does **not** expose gRPC services — it only consumes gRPC client stubs from other platform services.

**Tech Stack:** Kotlin 2.0.21, Ktor 3.0.3 (CIO), Koin 4.0.3, Exposed 0.57.0 + PostgreSQL, ClickHouse, Redis (Jedis), RabbitMQ, JVM 21

## Build Commands

```bash
./gradlew build                # Build everything
./gradlew test                 # Run all tests
./gradlew run                  # Run the application
./gradlew buildFatJar          # Build executable JAR with all dependencies
./gradlew test --tests "com.nekgambling.SomeTestClass"  # Run a single test class
./gradlew test --tests "com.nekgambling.SomeTestClass.someMethod"  # Run a single test method
```

**Note:** Tests require a running ClickHouse instance at `localhost:8123` (start with `docker-compose up clickhouse`).

## Infrastructure

```bash
docker-compose up -d           # Start all infrastructure (postgres, clickhouse, redis, rabbitmq)
docker-compose up clickhouse   # Start only ClickHouse (needed for tests)
```

## Architecture

Hexagonal Architecture + DDD + CQRS. Package base: `com.nekgambling`

```
api/                      → HTTP entry point + CommandBus dispatching
  EventApi.kt             → Single POST /event endpoint (returns 201)
  command/                → CommandBus, ICommand, ICommandHandler, 3 command handlers
  dto/                    → EventRequest, EventPayload (sealed interface with 4 payload types)
application/              → Queries, port interfaces, events
  adapter/                → Port interfaces: IEventAdapter, ILockAdapter, ICurrencyAdapter
  event/                  → Domain event definitions (sealed interfaces per aggregate)
  query/                  → QueryBus + 3 query types (bonus payout, invoice total, spin total)
domain/                   → Pure business logic
  model/journey/          → Journey graph: IJourneyNode (abstract), Journey, JourneyInstant
  model/player/           → Player aggregates: Details, Bonus, Freespin, Invoice, Spin
  repository/             → Repository ports: IJourneyRepository, IJourneyInstantRepository
  repository/player/      → Player repository ports
  strategy/               → JourneyNodeProcess strategy interface
  shared/vo/              → Value objects: Currency, Country, Locale, Period
  shared/param/           → Parameter value types: NumberParamValue, BoolParamValue, etc.
infrastructure/           → All adapters and implementations
  koin.kt                 → Single Koin module wiring everything (infrastructureModule)
  database/clickhouse/    → ClickHouseClient (raw JDBC), repositories, query handlers, table constants
  database/exposed/       → PostgreSQL via Exposed ORM (journeys, journey nodes, journey instants)
  external/rabbitmq/      → RabbitMQEventAdapter + event mappers (topic exchange: crm.events)
  external/redis/         → RedisLockAdapter (distributed lock with Lua script release)
  external/currency/      → UnitsCurrencyAdapter (amount * 100 conversion)
  journey/player/         → PlayerJourneyNode + 5 player definition evaluators
  journey/trigger/        → Trigger journey nodes (bonus, freespin, invoice, segment)
  journey/action/         → Action journey nodes (IActionJourneyNode base) + processors
  journey/action/push/    → Push action nodes (email, SMS, in-app) with template + placeholders
```

## Key Patterns

### Event Ingestion Flow
`POST /event` → `EventApi` deserializes `EventRequest` → `CommandBus` dispatches to handler → handler persists to ClickHouse + publishes event via `IEventAdapter`.

### CQRS Buses
- **CommandBus**: Dispatches `ICommand<R>` to matching `ICommandHandler` by `commandType: KClass`. All 3 commands return `Unit`. Command handlers contain the full business logic (no separate use case layer).
- **QueryBus**: Dispatches `IQuery<R>` to matching `IQueryHandler` by `queryType: KClass`. Query handlers read from ClickHouse materialized views.

### Journey Node Architecture
`IJourneyNode` is an abstract class with `id`, `next`, `inputParams()`, and `outputParams()`. Three node categories:
- **PlayerJourneyNode**: Evaluates player definitions (`IPlayerDefinition`) with match/mismatch branching
- **ITriggerJourneyNode**: Matches incoming event payload against node criteria (bonus, freespin, invoice, segment triggers)
- **IActionJourneyNode**: Executes side-effect actions (e.g., push notifications). Subcategory `IPushActionJourneyNode` (sealed class with `templateId` + `placeHolders`) has three concrete types: `EMailPushActionJourneyNode`, `SmsPushActionJourneyNode`, `InAppPushActionJourneyNode`

`JourneyNodeProcess<N>` strategy interface processes nodes. `IActionJourneyNodeProcess<T>` is the abstract base for action processors; `IPushActionJourneyNodeProcess` handles all push subtypes. `IPlayerDefinitionEvaluator<T>` implementations evaluate player conditions. Five definition types: `playerAge`, `profileField`, `spinTotal`, `invoiceTotal`, `playerGgr`.

### Dual Database Design
- **ClickHouse** (analytics): Player activity data in `ReplacingMergeTree` tables with `_version` for upserts. Two `SummingMergeTree` tables (`player_invoice_total`, `player_total_spin_info`) fed by materialized views for pre-aggregated queries.
- **PostgreSQL** (journey state): Journey definitions, journey nodes (single-table inheritance with type discriminator), journey instants (player progress tracking) via Exposed ORM.

### Distributed Locking
Redis-based locks (10s TTL, 50ms retry) prevent concurrent evaluation races.

### Idempotency
Command handlers check for existing records before creating (invoice by ID, spin by composite key) to safely handle duplicate events.

## Event Types Published (RabbitMQ)

Exchange `crm.events` (topic). Routing keys: `bonus.issued`, `bonus.lost`, `bonus.start_wagering`, `bonus.wagered`, `freespin.issued`, `freespin.activated`, `freespin.canceled`, `freespin.played`, `spin.opened`, `spin.closed`, `invoice.created`, `invoice.updated`, `player.registered`, `player.updated`.

## ClickHouse Schema

SQL init scripts at `src/main/resources/clickhouse/init.sql` and `clickhouse/meterized_view.sql`. Tables auto-created on startup via `ClickHouseClient.initTables()`.

## Testing

- Framework: `kotlin-test` + `MockK` + `kotlinx-coroutines-test`
- All tests extend `AbstractClickHouseTest` which truncates ClickHouse tables in `@BeforeTest`
- `IEventAdapter` and `ICurrencyAdapter` are mocked; ClickHouse repositories use real JDBC
- Tests require a running ClickHouse instance (default: `localhost:8123`, user `default`, no password)

## Environment Variables

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `CLICKHOUSE_URL` | Yes | — | `jdbc:clickhouse://host:8123/default` |
| `CLICKHOUSE_USERNAME` | No | `default` | ClickHouse user |
| `CLICKHOUSE_PASSWORD` | No | `""` | ClickHouse password |
| `DB_URL` | Yes | — | PostgreSQL JDBC URL |
| `DB_USERNAME` | Yes | — | PostgreSQL user |
| `DB_PASSWORD` | Yes | — | PostgreSQL password |
| `REDIS_HOST` | Yes | — | Redis host |
| `REDIS_PORT` | No | `6379` | Redis port |
| `REDIS_PASSWORD` | No | — | Redis password |
| `RABBITMQ_HOST` | Yes | — | RabbitMQ host |
| `RABBITMQ_PORT` | No | `5672` | RabbitMQ port |
| `RABBITMQ_USERNAME` | Yes | — | RabbitMQ user |
| `RABBITMQ_PASSWORD` | Yes | — | RabbitMQ password |
| `RABBITMQ_EXCHANGE` | No | `crm.events` | RabbitMQ exchange name |
| `RABBITMQ_EXCHANGE_TYPE` | No | `topic` | RabbitMQ exchange type |

## Conventions

- **Monetary values**: `Long` in minor units (cents). `ICurrencyAdapter.convertToUnits()` converts `Double` → `Long` via `* 100`.
- **Naming**: Commands `Process<Entity>Command`, Handlers `Process<Entity>CommandHandler`, Repositories `I<Entity>Repository` / `ClickHouse<Entity>Repository` / `Exposed<Entity>Repository`.
- **DI**: Single `infrastructureModule` in `infrastructure/koin.kt`. Multi-binding via `bind Interface::class` + `getAll()` for buses and evaluators. Everything is `single` scoped.
- **Player definitions**: Polymorphic serialization via `@Serializable` + `@SerialName`. New definitions need: data class implementing `IPlayerDefinition`, evaluator implementing `IPlayerDefinitionEvaluator<T>`, Koin binding, and registration in `PlayerDefinitionJson.kt` serializers module.

## Adding a New Player Definition

1. Create data class in `infrastructure/journey/player/yourRule/` implementing `IPlayerDefinition` with `@Serializable @SerialName("yourRule")`
2. Create evaluator in same package implementing `IPlayerDefinitionEvaluator<YourDefinition>`
3. Register serializer in `infrastructure/database/exposed/mapper/PlayerDefinitionJson.kt` polymorphic module
4. Add Koin binding in `infrastructure/koin.kt`: `single { YourDefinitionEvaluator(...) } bind IPlayerDefinitionEvaluator::class`

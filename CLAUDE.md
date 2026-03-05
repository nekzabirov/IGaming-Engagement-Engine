# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**crm-engine** is a Kotlin/Ktor CRM data processing service for the IGambling platform. It ingests player lifecycle events (registrations, deposits, withdrawals, casino bets) via a single HTTP endpoint, stores analytics data in ClickHouse, evaluates player segmentation conditions, and publishes domain events to RabbitMQ. It does **not** expose gRPC services — it only consumes gRPC client stubs from other platform services.

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
api/                → HTTP entry point + CommandBus dispatching
  EventApi.kt       → Single POST /event endpoint (returns 201)
  command/          → CommandBus, ICommand, ICommandHandler, 3 command handlers
  dto/              → EventRequest, EventPayload (sealed interface with 4 payload types)
application/        → Use cases, queries, port interfaces
  adapter/          → Port interfaces: IEventAdapter, ILockAdapter, ICurrencyAdapter
  query/            → QueryBus + 3 query types (bonus payout, invoice total, spin total)
  usecase/          → 13 player use cases + 2 segment use cases
domain/             → Pure business logic
  condition/        → Condition model, IConditionRule, evaluator strategy pattern
  event/            → Domain event definitions (sealed interfaces per aggregate)
  journey/          → Journey node graph (IJourneyNode), node processing strategy (JourneyNodeProcess)
  player/           → Player aggregates (Details, Bonus, Freespin, Invoice, Spin) + repository ports
  segment/          → Segment model + repository port
  trigger/          → Trigger model (ITrigger) + processing strategy (ITriggerProcessStrategy)
  vo/               → Value objects: Currency, Country, Locale, Period
infrastructure/     → All adapters and implementations
  koin.kt           → Single Koin module wiring everything (infrastructureModule)
  clickhouse/       → ClickHouseClient (raw JDBC), repositories, query handlers, table constants
  condition/        → 5 condition rule evaluator implementations
  journey/          → Journey node implementations (condition/, trigger/) + node processors
  exposed/          → PostgreSQL via Exposed ORM (conditions, segments, results)
  rabbitmq/         → RabbitMQEventAdapter + event mappers (topic exchange: crm.events)
  redis/            → RedisLockAdapter (distributed lock with Lua script release)
  currency/         → UnitsCurrencyAdapter (amount * 100 conversion)
```

## Key Patterns

### Event Ingestion Flow
`POST /event` → `EventApi` deserializes `EventRequest` → `CommandBus` dispatches to handler → handler invokes use case → use case persists to ClickHouse + publishes event via `IEventAdapter`.

### CQRS Buses
- **CommandBus**: Dispatches `ICommand<R>` to matching `ICommandHandler` by `commandType: KClass`. All 3 commands return `Unit`.
- **QueryBus**: Dispatches `IQuery<R>` to matching `IQueryHandler` by `queryType: KClass`. Query handlers read from ClickHouse materialized views.

### Condition Rule Strategy
`IConditionRule` (marker interface) is stored as polymorphic JSONB in PostgreSQL. `IConditionRuleEvaluator<T>` implementations are collected via Koin `getAll()` and resolved by `ConditionRuleEvaluatorResolver` using `KClass.isInstance()`. Five rule types: `playerAge`, `profileField`, `spinTotal`, `invoiceTotal`, `playerGgr`.

### Dual Database Design
- **ClickHouse** (analytics): Player activity data in `ReplacingMergeTree` tables with `_version` for upserts. Two `SummingMergeTree` tables (`player_invoice_total`, `player_total_spin_info`) fed by materialized views for pre-aggregated queries.
- **PostgreSQL** (metadata): Condition rules (JSONB), condition results, segment results via Exposed ORM.

### Distributed Locking
Redis-based locks (10s TTL, 50ms retry) on keys `condition:{id}:player:{playerId}` and `segment:{id}:player:{playerId}` prevent concurrent evaluation races.

### Idempotency
Command handlers check for existing records before creating (invoice by ID, spin by composite key) to safely handle duplicate events.

## Event Types Published (RabbitMQ)

Exchange `crm.events` (topic). Routing keys: `bonus.issued`, `bonus.lost`, `bonus.start_wagering`, `bonus.wagered`, `freespin.issued`, `freespin.activated`, `freespin.canceled`, `freespin.played`, `spin.opened`, `spin.closed`, `invoice.created`, `invoice.updated`, `player.registered`, `player.updated`, `segment.result`, `condition.result`.

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
- **Naming**: Commands `Process<Entity>Command`, Handlers `Process<Entity>CommandHandler`, Use cases `<Verb><Entity>UseCase`, Repositories `I<Entity>Repository` / `ClickHouse<Entity>Repository` / `Exposed<Entity>Repository`.
- **DI**: Single `infrastructureModule` in `infrastructure/koin.kt`. Multi-binding via `bind Interface::class` + `getAll()` for buses and evaluators. Use cases are `factory` scoped; everything else is `single`.
- **Condition rules**: Polymorphic serialization via `@Serializable` + `@SerialName`. New rules need: data class implementing `IConditionRule`, evaluator implementing `IConditionRuleEvaluator<T>`, Koin binding, and registration in `ConditionRuleJson.kt` serializers module.

## Adding a New Condition Rule

1. Create `domain/condition/` or reuse existing model for `IConditionRule` subclass with `@Serializable @SerialName("yourRule")`
2. Create evaluator in `infrastructure/condition/yourRule/` implementing `IConditionRuleEvaluator<YourRule>`
3. Register serializer in `infrastructure/exposed/ConditionRuleJson.kt` polymorphic module
4. Add Koin binding in `infrastructure/koin.kt`: `single { YourRuleEvaluator(...) } bind IConditionRuleEvaluator::class`

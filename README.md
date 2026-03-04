# CRM Engine

An open-source, event-driven CRM data processing engine built with Kotlin. It ingests player lifecycle events, stores analytics in ClickHouse, evaluates segmentation rules, and publishes domain events via RabbitMQ.

Designed as a standalone service — pluggable into any platform that produces player activity events. The core value lives in the domain logic, condition engine, and analytics pipeline.

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin 2.0 | Language |
| Ktor 3.0 (CIO) | HTTP server |
| Koin 4.0 | Dependency injection |
| ClickHouse | Analytics storage (OLAP) |
| PostgreSQL + Exposed ORM | Metadata storage (conditions, segments) |
| Redis (Jedis) | Distributed locking |
| RabbitMQ | Domain event publishing |
| kotlinx.serialization | JSON serialization |
| JVM 21 | Runtime |

## Architecture

Hexagonal Architecture (Ports & Adapters) with DDD and CQRS.

```
┌─────────────────────────────────────────────────────────┐
│  Inbound                                                │
│  Event → CommandBus → Handler → Use Case                │
├─────────────────────────────────────────────────────────┤
│  Domain                                                 │
│  Entities, Value Objects, Conditions, Triggers,         │
│  Segments, Journeys, Repository Ports                   │
├─────────────────────────────────────────────────────────┤
│  Outbound                                               │
│  ClickHouse Repos │ Exposed Repos │ RabbitMQ │ Redis    │
└─────────────────────────────────────────────────────────┘
```

**Package structure:**

```
domain/             → Pure business logic (zero dependencies on infrastructure)
  condition/        → Condition model, evaluator strategy pattern
  trigger/          → Trigger model, synchronous rule matching
  journey/          → Journey node graph (condition-based branching)
  segment/          → Segment model (composite conditions)
  player/           → Player aggregates (Details, Bonus, Freespin, Invoice, Spin)
  shared/           → Value objects (Currency, Country, Locale, Period) and param types
application/        → Use cases, queries, port interfaces
  adapter/          → Port interfaces (IEventAdapter, ILockAdapter, ICurrencyAdapter)
  query/            → QueryBus + query types (read from ClickHouse aggregations)
  usecase/          → 13 player use cases + 2 segment use cases
infrastructure/     → All adapter implementations
  clickhouse/       → ClickHouseClient, repositories, query handlers
  exposed/          → PostgreSQL tables, repositories, polymorphic serialization
  rabbitmq/         → Event publishing + routing key mappers
  redis/            → Distributed lock adapter
  condition/        → 5 condition rule evaluator implementations
  trigger/          → Trigger rule implementations
  currency/         → Amount conversion adapter
```

### CQRS

- **Commands** modify state: `CommandBus` dispatches `ICommand<R>` to a matching `ICommandHandler`. All commands return `Unit`.
- **Queries** read state: `QueryBus` dispatches `IQuery<R>` to a matching `IQueryHandler`. Query handlers read from ClickHouse materialized views.

Both buses resolve handlers at runtime via Koin multi-binding (`getAll()`) and `KClass.isInstance()`.

### Dual Database Design

| Database | Engine | Purpose |
|---|---|---|
| ClickHouse | `ReplacingMergeTree` | Raw player activity data (upserts via `_version`) |
| ClickHouse | `SummingMergeTree` | Pre-aggregated totals fed by materialized views |
| PostgreSQL | Exposed ORM | Condition rules (JSONB), condition/segment results |

**ClickHouse tables:**

| Table | Engine | Data |
|---|---|---|
| `player_details` | ReplacingMergeTree | Player profile snapshots |
| `player_bonus` | ReplacingMergeTree | Bonus lifecycle records |
| `player_spin` | ReplacingMergeTree | Casino bet/settle records |
| `player_freespin` | ReplacingMergeTree | Free spin lifecycle |
| `player_invoice` | ReplacingMergeTree | Deposit/withdrawal records |
| `player_invoice_total` | SummingMergeTree | Daily aggregated invoice totals |
| `player_total_spin_info` | SummingMergeTree | Daily aggregated spin totals |

Materialized views automatically feed the `SummingMergeTree` tables on insert.

### Distributed Locking

Redis-based locks (10s TTL, 50ms retry, Lua script release) prevent concurrent evaluation races on keys like `condition:{id}:player:{playerId}`.

### Idempotency

Command handlers check for existing records before creating (invoice by ID, spin by composite key) to safely handle duplicate events.

---

## Business Logic

### Event Ingestion Flow

Events arrive and flow through a command pipeline:

```
Incoming Event
    ↓
Deserialize EventRequest (event_type, user_ext_id, payload)
    ↓
Pattern-match payload type → create Command
    ↓
CommandBus dispatches to Handler
    ↓
Handler invokes Use Case (idempotency check → persist to ClickHouse → publish event)
    ↓
RabbitMQ publishes domain event with routing key
```

**Supported payload types:**

| Payload | Command | Use Cases |
|---|---|---|
| `UserPayload` | `ProcessPlayerCommand` | UpdatePlayer |
| `DepositApprovePayload` | `ProcessInvoiceCommand` | CreateInvoice, UpdateInvoice |
| `WithdrawPayload` | `ProcessInvoiceCommand` | CreateInvoice, UpdateInvoice |
| `CasinoBetWinPayload` | `ProcessSpinCommand` | PlaceSpin, SettleSpin |

### Player Aggregates

The engine tracks five player data aggregates, each with its own ClickHouse table and repository:

- **PlayerDetails** — Profile: username, email, phone, status, country, locale, gender, verification, affiliate tag
- **PlayerBonus** — Bonus lifecycle: ISSUE → WAGERING → WAGERED/LOST, amounts in minor units
- **PlayerInvoice** — Financial transactions: DEPOSIT/PAYOUT/REFUND with status tracking, multi-currency
- **PlayerSpin** — Casino bets: place/settle amounts split by real/bonus, linked to games and freespins
- **PlayerFreespin** — Free spin lifecycle: ISSUE → ACTIVE → PLAYED/CANCELLED, payout tracking

### Condition Engine

Conditions are rules evaluated against player data. Each condition has a **rule** (what to check) and produces a **result** (passed/failed per player).

Condition rules use a **strategy pattern**: `IConditionRuleEvaluator<T>` implementations are collected via Koin multi-binding and resolved by `ConditionRuleEvaluatorResolver` using runtime type matching.

**Built-in condition rules:**

| Rule | `@SerialName` | Evaluates |
|---|---|---|
| Player Age | `playerAge` | Player's age in years against a numeric threshold |
| Profile Field | `profileField` | Any of 17 profile fields against a param value |
| Spin Total | `spinTotal` | Aggregated place/settle amounts over a date range |
| Invoice Total | `invoiceTotal` | Aggregated deposit/withdraw amounts and counts over a date range |
| Player GGR | `playerGgr` | Gross/Net Gaming Revenue over a date range |

**Condition rule JSON examples:**

```json
{
  "type": "playerAge",
  "value": { "type": "numberMore", "value": 18 }
}
```

```json
{
  "type": "spinTotal",
  "placeAmount": { "type": "numberMore", "value": 100000 },
  "settleAmount": null,
  "date": { "type": "dateLastDays", "days": 30 }
}
```

```json
{
  "type": "invoiceTotal",
  "depositCount": { "type": "numberMore", "value": 3 },
  "depositAmount": { "type": "numberMore", "value": 50000 },
  "withdrawCount": null,
  "withdrawAmount": null,
  "taxAmount": null,
  "feesAmount": null,
  "date": { "type": "dateRange", "from": "2025-01-01T00:00:00Z", "until": "2025-12-31T23:59:59Z" }
}
```

```json
{
  "type": "profileField",
  "field": "COUNTRY",
  "value": { "type": "string", "value": "US" }
}
```

```json
{
  "type": "playerGgr",
  "ggr": { "type": "numberMore", "value": 500000 },
  "ngr": null,
  "date": { "type": "dateLastDays", "days": 90 }
}
```

### Param Value Types

Condition rules use polymorphic param values for flexible comparisons:

| Param | `@SerialName` | Purpose |
|---|---|---|
| `StringParamValue` | `string` | Exact string match |
| `NumberMoreParamValue` | `numberMore` | Greater-than numeric check |
| `BoolParamValue` | `bool` | Boolean match |
| `NullEmptyParamValue` | `nullEmpty` | Matches null or blank values |
| `DateBeforeParamValue` | `dateBefore` | Before a specific date |
| `DateAfterParamValue` | `dateAfter` | After a specific date |
| `DateRangeParamValue` | `dateRange` | Between two dates |
| `DateLastDays` | `dateLastDays` | Rolling window (last N days) |

### Trigger Engine

Triggers are **synchronous** rules that match against incoming event payloads. Unlike conditions (which query stored data), triggers evaluate immediately against the event that fired them.

- Rule properties = filter criteria (configured by admins)
- Payload = actual event data to match against
- Nullable rule properties = "skip this check" (match any value)
- `buildOutput()` produces key-value pairs for downstream consumers (e.g., journey nodes)

**Example trigger rule JSON (invoice trigger):**

```json
{
  "type": "invoice",
  "type_filter": "DEPOSIT",
  "status": "SUCCESS",
  "currency": "USD",
  "amount": { "type": "numberMore", "value": 10000 }
}
```

### Segments

A segment is a **composite of conditions**. A player belongs to a segment when **all** its conditions pass.

```
Segment
  ├── Condition 1 (playerAge > 21)
  ├── Condition 2 (depositCount > 5 in last 30 days)
  └── Condition 3 (country = "US")
      → Player passes segment only if ALL three conditions pass
```

Segment evaluation results are persisted and published as domain events.

### Journey Graph

Journeys model decision trees using a node graph:

```
ConditionJourneyNode
  ├── condition: Condition (evaluated per player)
  ├── matchNode: IJourneyNode?    → path if condition PASSES
  └── notMatchNode: IJourneyNode? → path if condition FAILS
```

Each node links to `prev`/`next` nodes, forming a traversable graph that branches based on condition evaluation results.

### Domain Events (RabbitMQ)

All events publish to the `crm.events` topic exchange with the following routing keys:

| Routing Key | Event |
|---|---|
| `bonus.issued` | Bonus issued to player |
| `bonus.lost` | Bonus lost |
| `bonus.start_wagering` | Bonus wagering started |
| `bonus.wagered` | Bonus fully wagered |
| `freespin.issued` | Free spin issued |
| `freespin.activated` | Free spin activated |
| `freespin.canceled` | Free spin canceled |
| `freespin.played` | Free spin played |
| `spin.opened` | Casino bet placed |
| `spin.closed` | Casino bet settled |
| `invoice.created` | Deposit/withdrawal created |
| `invoice.updated` | Deposit/withdrawal status updated |
| `player.registered` | New player registered |
| `player.updated` | Player profile updated |
| `segment.result` | Segment evaluation result |
| `condition.result` | Condition evaluation result |

---

## Getting Started

### Prerequisites

- JDK 21+
- Docker & Docker Compose

### Run Infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL, ClickHouse, Redis, and RabbitMQ.

### Build & Run

```bash
./gradlew build
./gradlew run
```

### Run Tests

Tests require a running ClickHouse instance:

```bash
docker-compose up -d clickhouse
./gradlew test
```

### Build Fat JAR

```bash
./gradlew buildFatJar
```

### Docker

```bash
docker-compose up -d crm-engine
```

---

## Configuration

All configuration is via environment variables:

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `CLICKHOUSE_URL` | Yes | — | JDBC URL (`jdbc:clickhouse://host:8123/default`) |
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
| `RABBITMQ_EXCHANGE` | No | `crm.events` | Exchange name |
| `RABBITMQ_EXCHANGE_TYPE` | No | `topic` | Exchange type |

ClickHouse tables are auto-created on startup. PostgreSQL schema is managed by Exposed ORM.

---

## Extending the Engine

### Adding a New Condition Rule

1. **Create the rule data class** in `infrastructure/condition/<name>/`:

```kotlin
@Serializable
@SerialName("yourRuleName")
data class YourConditionRule(
    val someMetric: NumberParamValue? = null,
    val date: DateParamValue,
) : IConditionRule
```

2. **Create the evaluator** in the same package:

```kotlin
class YourConditionRuleEvaluator(
    private val queryBus: QueryBus,
) : IConditionRuleEvaluator<YourConditionRule> {
    override val condition: KClass<YourConditionRule> = YourConditionRule::class

    override suspend fun evaluate(playerId: String, condition: YourConditionRule): Boolean {
        val total = queryBus.execute(GetSomeQuery(playerId, condition.date.toPeriod()))
        condition.someMetric?.let { if (!it.check(total.value)) return false }
        return true
    }
}
```

3. **Register the polymorphic serializer** in `infrastructure/exposed/ConditionRuleJson.kt`:

```kotlin
polymorphic(IConditionRule::class) {
    // ... existing rules
    subclass(YourConditionRule::class)
}
```

4. **Register in Koin** in `infrastructure/koin.kt`:

```kotlin
single { YourConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class
```

If the rule needs data not served by existing queries, you'll also need to create a new query + ClickHouse handler (and optionally new ClickHouse tables/materialized views).

### Adding a New Trigger Rule

1. **Create the trigger rule** in `infrastructure/trigger/`:

```kotlin
@Serializable
@SerialName("yourTrigger")
data class YourTriggerRule(
    val type: SomeEnum,                    // Required match
    val currency: Currency? = null,        // Optional filter
    val amount: NumberParamValue? = null,  // Optional numeric check
) : ITriggerRule<YourTriggerRule.Payload> {

    data class Payload(
        val type: SomeEnum,
        val currency: Currency,
        val amount: Long,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = mapOf(
            "currency" to currency.toString(),
            "amount" to amount.toString(),
        )
    }

    override fun evaluate(payload: Payload): Boolean =
        payload.type == type
            && (currency == null || payload.currency == currency)
            && (amount == null || amount.check(payload.amount))
}
```

2. **Register the polymorphic serializer** in `infrastructure/exposed/TriggerRuleJson.kt`.

3. **Wire payload construction** from incoming event data in the trigger evaluation pipeline.

### Adding a New ClickHouse Query

1. Create `GetYourQuery` in `application/query/player/` implementing `IQuery<Result>`
2. Create `ClickHouseYourQueryHandler` in `infrastructure/clickhouse/query/`
3. If needed, add tables to `src/main/resources/clickhouse/init.sql` and materialized views to `meterized_view.sql`
4. Register handler in Koin: `single { ClickHouseYourQueryHandler(get()) } bind IQueryHandler::class`

---

## Claude Code Integration

This project includes [Claude Code](https://claude.ai/code) configuration for AI-assisted development.

### Rules (`.claude/rules/`)

Rules provide architectural and coding guidelines that Claude follows when working on this codebase:

| Rule File | Purpose |
|---|---|
| `architecture.md` | Hexagonal architecture boundaries, CQRS rules, DI patterns |
| `coding-conventions.md` | Naming conventions, monetary values, serialization, concurrency |
| `testing.md` | Test setup, mocking patterns, running tests |

### Skills (`.claude/skills/`)

Skills are guided workflows that Claude follows step-by-step to create new features:

| Skill | Purpose |
|---|---|
| `new-condition-rule` | Creates a complete `IConditionRule` implementation: rule data class, evaluator, serializer registration, Koin binding, and optionally a new query pipeline |
| `new-trigger-rule` | Creates a complete `ITriggerRule` implementation: rule + payload data class, evaluate logic, serializer registration, and wiring guidance |

**Usage:** Describe what you want in natural language and Claude will follow the skill's steps. Examples:

- *"Create a condition rule that checks if a player has placed more than 100 spins in the last 7 days"*
- *"Create a trigger rule that fires when a player's bonus is wagered"*

---

## Conventions

- **Monetary values**: Always `Long` in minor units (cents). `ICurrencyAdapter.convertToUnits()` handles conversion.
- **Naming**: Commands `Process<Entity>Command`, Use cases `<Verb><Entity>UseCase`, Repositories `I<Entity>Repository` / `ClickHouse<Entity>Repository` / `Exposed<Entity>Repository`
- **Condition rules**: Polymorphic serialization via `@Serializable` + `@SerialName("<camelCase>")`, discriminator field is `"type"`
- **DI**: Single `infrastructureModule`. Multi-binding via `bind Interface::class` + `getAll()`. Use cases are `factory` scoped; everything else is `single`.
- **ClickHouse reads**: Always use `SELECT ... FINAL` on `ReplacingMergeTree` tables.
- **Outbound adapters**: Each adapter has its own `<Name>Config` data class reading env vars; port interfaces never read env vars directly.

## License

Open source. See [LICENSE](LICENSE) for details.

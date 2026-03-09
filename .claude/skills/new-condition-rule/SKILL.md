Create a new IPlayerDefinition implementation in the crm-engine project.

## Input

The user will describe what the player definition should evaluate (e.g., "player session count", "total gold points", "player login streak"). Parse from their description:
- **Definition name** (e.g., `sessionCount`, `goldPoints`, `loginStreak`)
- **What data the evaluator needs** to make the decision

## Step 1: Determine data source

Check if an existing query in `src/main/kotlin/application/query/player/` already provides the required data:
- `GetPlayerSpinTotalQuery` → placeAmount, settleAmount, realPlaceAmount, realSettleAmount
- `GetPlayerInvoiceTotalQuery` → depositAmount, withdrawAmount, depositCount, withdrawCount, taxAmount, feesAmount
- `GetPlayerBonusPayoutTotalQuery` → Long (total bonus + freespin payouts)

If NO existing query provides the data, STOP and ask the user:

> "There is no existing query for this data. I need to create a new query pipeline. Here's what I propose:
>
> 1. **Query**: `Get<Entity>Query` in `application/query/player/` with a `Result` data class containing [proposed fields]
> 2. **Data source**: Read from ClickHouse table `<table_name>` — [explain whether this is an existing table or needs a new one]
> 3. **Aggregation**: [If reading from a SummingMergeTree aggregation table, explain the materialized view that feeds it. If reading from a raw ReplacingMergeTree table with FINAL, explain that too]
> 4. **ClickHouse handler**: `ClickHouse<Entity>QueryHandler` in `infrastructure/database/clickhouse/query/`
>
> Should I proceed with this approach?"

Wait for user confirmation before continuing.

## Step 2: Create the player definition data class

Create file: `src/main/kotlin/infrastructure/journey/player/<name>/<Name>PlayerDefinition.kt`

```kotlin
package com.nekgambling.infrastructure.journey.player.<name>

import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import com.nekgambling.domain.shared.param.DateParamValue
import com.nekgambling.domain.shared.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("<camelCaseName>")
data class <Name>PlayerDefinition(
    // NumberParamValue? fields for each checkable metric (nullable = optional check)
    // val someMetric: NumberParamValue? = null,

    // DateParamValue if the definition needs a time period for aggregation queries
    // val date: DateParamValue,
) : IPlayerDefinition
```

Guidelines:
- Use `NumberParamValue?` for numeric thresholds that can be optionally checked
- Use `DateParamValue` when the evaluator queries aggregated data over a time period
- Use `ParamValue` for generic field comparisons
- Use `BoolParamValue` for boolean checks
- `@SerialName` must be camelCase matching the definition concept

## Step 3: Create the evaluator

Create file: `src/main/kotlin/infrastructure/journey/player/<name>/<Name>PlayerDefinitionEvaluator.kt`

```kotlin
package com.nekgambling.infrastructure.journey.player.<name>

import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlin.reflect.KClass

class <Name>PlayerDefinitionEvaluator(
    // Inject QueryBus if using queries, or specific repository if reading directly
    // private val queryBus: QueryBus,
    // private val playerDetailsRepository: IPlayerDetailsRepository,
) : IPlayerDefinitionEvaluator<<Name>PlayerDefinition> {
    override val definition: KClass<<Name>PlayerDefinition> = <Name>PlayerDefinition::class

    override suspend fun evaluate(playerId: String, condition: <Name>PlayerDefinition): Boolean {
        // Pattern for QueryBus usage:
        // val total = queryBus.execute(GetSomethingQuery(playerId, condition.date.toPeriod()))
        // condition.someField?.let { if (!it.check(total.someField)) return false }
        // return true

        // Pattern for repository usage:
        // val player = repository.findById(playerId).orElse(null) ?: return false
        // return condition.value.check(computedValue)
    }
}
```

## Step 4: If a new query is needed, create the full pipeline

### 4a. Query data class
Create: `src/main/kotlin/application/query/player/Get<Entity>Query.kt`
```kotlin
data class Get<Entity>Query(
    val playerId: String,
    val period: Period,
) : IQuery<Get<Entity>Query.Result> {
    data class Result(
        // Long fields for amounts, Int for counts
    )
}
```

### 4b. ClickHouse query handler
Create: `src/main/kotlin/infrastructure/database/clickhouse/query/ClickHouse<Entity>QueryHandler.kt`
- Read from SummingMergeTree aggregation table if available, otherwise from raw table with FINAL
- Use `sum()` with `player_id = ?` and `date >= toDate(?) AND date <= toDate(?)` for period filtering
- Return zero-value Result as default

### 4c. ClickHouse schema (if new table needed)
- Add raw table to `src/main/resources/clickhouse/init.sql` using `ReplacingMergeTree(_version)`
- Add aggregation table using `SummingMergeTree`
- Add materialized view to `src/main/resources/clickhouse/meterized_view.sql`
- Add table name constant to `infrastructure/database/clickhouse/ClickHouseTable.kt`

### 4d. Register query handler in Koin
Add to `infrastructure/koin.kt` in the query handlers section:
```kotlin
single { ClickHouse<Entity>QueryHandler(get()) } bind IQueryHandler::class
```

## Step 5: Register the player definition

### 5a. Polymorphic serializer
In `src/main/kotlin/infrastructure/database/exposed/mapper/PlayerDefinitionJson.kt`, add inside the `polymorphic(IPlayerDefinition::class)` block:
```kotlin
subclass(<Name>PlayerDefinition::class)
```

### 5b. Koin DI
In `src/main/kotlin/infrastructure/koin.kt`, add in the player definition evaluators section:
```kotlin
single { <Name>PlayerDefinitionEvaluator(get()) } bind IPlayerDefinitionEvaluator::class
```

## Step 6: Summary

After creating all files, print a summary:
- Files created/modified
- The `@SerialName` discriminator value for API usage
- Example JSON for the player definition

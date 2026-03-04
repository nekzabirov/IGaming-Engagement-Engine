Create a new IConditionRule implementation in the crm-engine project.

## Input

The user will describe what the rule should evaluate (e.g., "player session count", "total gold points", "player login streak"). Parse from their description:
- **Rule name** (e.g., `sessionCount`, `goldPoints`, `loginStreak`)
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
> 4. **ClickHouse handler**: `ClickHouse<Entity>QueryHandler` in `infrastructure/clickhouse/query/`
>
> Should I proceed with this approach?"

Wait for user confirmation before continuing.

## Step 2: Create the condition rule data class

Create file: `src/main/kotlin/infrastructure/condition/<name>/<Name>ConditionRule.kt`

```kotlin
package com.nekgambling.infrastructure.condition.<name>

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.shared.param.DateParamValue
import com.nekgambling.domain.shared.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("<camelCaseName>")
data class <Name>ConditionRule(
    // NumberParamValue? fields for each checkable metric (nullable = optional check)
    // val someMetric: NumberParamValue? = null,

    // DateParamValue if the rule needs a time period for aggregation queries
    // val date: DateParamValue,
) : IConditionRule
```

Guidelines:
- Use `NumberParamValue?` for numeric thresholds that can be optionally checked
- Use `DateParamValue` when the evaluator queries aggregated data over a time period
- Use `ParamValue` for generic field comparisons
- Use `BoolParamValue` for boolean checks
- `@SerialName` must be camelCase matching the rule concept

## Step 3: Create the evaluator

Create file: `src/main/kotlin/infrastructure/condition/<name>/<Name>ConditionRuleEvaluator.kt`

```kotlin
package com.nekgambling.infrastructure.condition.<name>

import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlin.reflect.KClass

class <Name>ConditionRuleEvaluator(
    // Inject QueryBus if using queries, or specific repository if reading directly
    // private val queryBus: QueryBus,
    // private val playerDetailsRepository: IPlayerDetailsRepository,
) : IConditionRuleEvaluator<<Name>ConditionRule> {
    override val condition: KClass<<Name>ConditionRule> = <Name>ConditionRule::class

    override suspend fun evaluate(playerId: String, condition: <Name>ConditionRule): Boolean {
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
Create: `src/main/kotlin/infrastructure/clickhouse/query/ClickHouse<Entity>QueryHandler.kt`
- Read from SummingMergeTree aggregation table if available, otherwise from raw table with FINAL
- Use `sum()` with `player_id = ?` and `date >= toDate(?) AND date <= toDate(?)` for period filtering
- Return zero-value Result as default

### 4c. ClickHouse schema (if new table needed)
- Add raw table to `src/main/resources/clickhouse/init.sql` using `ReplacingMergeTree(_version)`
- Add aggregation table using `SummingMergeTree`
- Add materialized view to `src/main/resources/clickhouse/meterized_view.sql`
- Add table name constant to `infrastructure/clickhouse/ClickHouseTable.kt`

### 4d. Register query handler in Koin
Add to `infrastructure/koin.kt` in the query handlers section:
```kotlin
single { ClickHouse<Entity>QueryHandler(get()) } bind IQueryHandler::class
```

## Step 5: Register the condition rule

### 5a. Polymorphic serializer
In `src/main/kotlin/infrastructure/exposed/ConditionRuleJson.kt`, add inside the `polymorphic(IConditionRule::class)` block:
```kotlin
subclass(<Name>ConditionRule::class)
```

### 5b. Koin DI
In `src/main/kotlin/infrastructure/koin.kt`, add in the condition rule evaluators section (before `ConditionRuleEvaluatorResolver`):
```kotlin
single { <Name>ConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class
```

## Step 6: Summary

After creating all files, print a summary:
- Files created/modified
- The `@SerialName` discriminator value for API usage
- Example JSON for the condition rule

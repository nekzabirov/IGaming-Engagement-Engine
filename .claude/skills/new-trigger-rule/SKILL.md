Create a new ITriggerRule implementation in the crm-engine project.

## Input

The user will describe what event should trigger the rule (e.g., "player spin placed", "bonus wagered", "player registered"). Parse from their description:
- **Rule name** (e.g., `spin`, `bonus`, `playerRegistration`)
- **What payload fields** the rule needs to match against
- **Which fields are optional filters** (nullable) vs required matches

## Reference

Use `src/main/kotlin/infrastructure/trigger/InvoiceTriggerRule.kt` as the canonical example of a trigger rule implementation.

## Key Concepts

- `ITriggerRule<P>` defines a rule that evaluates a **Payload** synchronously — no external data fetching
- The rule's properties are the **filter criteria** (configured by admins)
- The `Payload` carries the **actual event data** to match against
- Nullable rule properties mean "skip this check" (match any value)
- `ITriggerRulePayload.buildOutput()` returns key-value pairs exposed to downstream consumers (e.g., journey nodes)

## Step 1: Identify the event source

Determine which player event the trigger reacts to. Check existing event types in `src/main/kotlin/application/event/player/`:
- `bonus/` — IBonusEvent (issued, lost, startWagering, wagered)
- `freespin/` — IFreespinEvent (issued, activated, canceled, played)
- `invoice/` — IInvoiceEvent (created, updated)
- `spin/` — ISpinEvent (opened, closed)
- `player/` — IPlayerEvent (registered, updated)

Identify which event fields map to rule filter criteria and which become payload data.

## Step 2: Create the trigger rule data class

Create file: `src/main/kotlin/infrastructure/trigger/<Name>TriggerRule.kt`

```kotlin
package com.nekgambling.infrastructure.trigger

import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.domain.trigger.model.ITriggerRulePayload
import com.nekgambling.domain.shared.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("<camelCaseName>")
data class <Name>TriggerRule(
    // Required match fields (exact equality check):
    // val type: SomeEnum,
    // val status: SomeEnum,

    // Optional filter fields (nullable = skip check):
    // val currency: Currency? = null,
    // val amount: NumberParamValue? = null,
) : ITriggerRule<<Name>TriggerRule.Payload> {

    data class Payload(
        // All event data fields (non-nullable — payload always has the data):
        // val type: SomeEnum,
        // val status: SomeEnum,
        // val currency: Currency,
        // val amount: Long,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = mapOf(
            // Key-value pairs for downstream consumers:
            // "currency" to currency.toString(),
            // "amount" to amount.toString(),
        )
    }

    override fun evaluate(payload: Payload): Boolean =
        // Chain checks: required exact matches + optional nullable checks
        // payload.type == type
        //         && payload.status == status
        //         && (currency == null || payload.currency == currency)
        //         && (amount == null || amount.check(payload.amount))
        TODO("Implement evaluation logic")
}
```

### Guidelines:
- **Required filters**: Use non-nullable enum/value fields with `==` checks (e.g., `payload.type == type`)
- **Optional filters**: Use nullable fields with `(field == null || ...)` pattern
- **NumberParamValue**: For numeric range/comparison checks — uses `check(value: Long): Boolean`
- **Payload fields**: Always non-nullable — the event data is always present
- **buildOutput()**: Convert all values to `String` — these feed into journey node context
- `@SerialName` must be camelCase matching the trigger concept (e.g., `"invoice"`, `"spin"`, `"bonus"`)

## Step 3: Register polymorphic serializer

Check if a trigger rule serializer module exists. If not, create `src/main/kotlin/infrastructure/exposed/TriggerRuleJson.kt`:

```kotlin
package com.nekgambling.infrastructure.exposed

import com.nekgambling.domain.trigger.model.ITriggerRule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val triggerRuleSerializersModule = SerializersModule {
    polymorphic(ITriggerRule::class) {
        subclass(<Name>TriggerRule::class)
    }
}

val triggerRuleJson = Json {
    serializersModule = triggerRuleSerializersModule
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
```

If it already exists, just add the new `subclass(<Name>TriggerRule::class)` entry.

## Step 4: Wire up trigger evaluation

Check `src/main/kotlin/domain/trigger/` for how triggers are processed (e.g., `ITriggerProcessStrategy`). The new rule's Payload must be constructed from the incoming event data at the point where triggers are evaluated.

If there is an existing trigger processing strategy or use case that maps events to trigger payloads, add a mapping for the new trigger rule type. If not, flag this to the user:

> "I've created the trigger rule. To wire it into the event flow, we need to construct `<Name>TriggerRule.Payload` from the incoming event data in the trigger evaluation pipeline. Where should this mapping live?"

## Step 5: Summary

After creating all files, print a summary:
- Files created/modified
- The `@SerialName` discriminator value for API/JSON usage
- Example JSON for the trigger rule configuration
- Example Payload construction from event data
- List of evaluate() checks (required vs optional)

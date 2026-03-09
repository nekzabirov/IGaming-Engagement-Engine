Create a new ITriggerJourneyNode implementation in the crm-engine project.

## Input

The user will describe what event should trigger the journey node (e.g., "player spin placed", "bonus wagered", "player registered"). Parse from their description:
- **Trigger name** (e.g., `spin`, `bonus`, `playerRegistration`)
- **What payload fields** the trigger needs to match against
- **Which fields are optional filters** (nullable) vs required matches

## Reference

Use `src/main/kotlin/infrastructure/journey/trigger/invoice/InvoiceTriggerJourneyNode.kt` and `InvoiceTriggerJourneyNodeProcess.kt` as the canonical example.

## Key Concepts

- `ITriggerJourneyNode` extends `IJourneyNode` — it's a journey graph node that matches incoming event payload
- The node's properties are the **filter criteria** (configured by admins)
- The process implementation receives `(playerId, node, payload)` and returns `Response?` (null = no match)
- `inputParams()` declares required payload keys; `outputParams()` declares produced output keys
- Nullable node properties mean "skip this check" (match any value)

## Step 1: Identify the event source

Determine which player event the trigger reacts to. Check existing event types in `src/main/kotlin/application/event/player/`:
- `bonus/` — IBonusEvent (issued, lost, startWagering, wagered)
- `freespin/` — IFreespinEvent (issued, activated, canceled, played)
- `invoice/` — IInvoiceEvent (created, updated)
- `spin/` — ISpinEvent (opened, closed)
- `player/` — IPlayerEvent (registered, updated)

Identify which event fields map to trigger filter criteria and which become payload output data.

## Step 2: Create the trigger journey node data class

Create file: `src/main/kotlin/infrastructure/journey/trigger/<name>/<Name>TriggerJourneyNode.kt`

```kotlin
package com.nekgambling.infrastructure.journey.trigger.<name>

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class <Name>TriggerJourneyNode(
    private val _id: Long = Long.MIN_VALUE,
    // Filter fields (nullable = optional):
    // val someField: SomeType? = null,
    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = _id, next = _next) {

    override fun inputParams(): Set<String> = setOf(
        // Required payload keys this trigger reads from
    )

    override fun outputParams(): Set<String> = setOf(
        // Output keys this trigger produces for downstream nodes
    )
}
```

### Guidelines:
- Prefix payload keys with entity name to avoid collisions (e.g., `invoiceType`, `bonusStatus`)
- `_id` and `_next` are private constructor params forwarded to `ITriggerJourneyNode` super

## Step 3: Create the trigger node process

Create file: `src/main/kotlin/infrastructure/journey/trigger/<name>/<Name>TriggerJourneyNodeProcess.kt`

```kotlin
package com.nekgambling.infrastructure.journey.trigger.<name>

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class <Name>TriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<<Name>TriggerJourneyNode> {
    override val nodeType: KClass<<Name>TriggerJourneyNode> = <Name>TriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: <Name>TriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        // Extract fields from payload
        // val someField = payload["someField"] as? String ?: return null

        // Check required fields match
        // if (node.someField != null && node.someField != extractedValue) return null

        // Return response with next node and output
        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                // "outputKey" to outputValue,
            ),
        )
    }
}
```

## Step 4: Add persistence support

### 4a. JourneyNodesTable columns
Add nullable columns in `src/main/kotlin/infrastructure/database/exposed/table/JourneyNodesTable.kt`:
```kotlin
// <Name>TriggerJourneyNode
val <fieldName> = varchar("<field_name>", 255).nullable()
```

### 4b. JourneyNodeEntity mapping
In `src/main/kotlin/infrastructure/database/exposed/entity/JourneyNodeEntity.kt`:
1. Add entity fields: `var <fieldName> by JourneyNodesTable.<fieldName>`
2. Add case to `toDomain()` `when` block:
```kotlin
<Name>TriggerJourneyNode::class.simpleName -> <Name>TriggerJourneyNode(
    _id = id.value,
    // map entity fields to constructor params
    _next = nextDomain,
)
```

## Step 5: Summary

After creating all files, print a summary:
- Files created/modified
- List of filter criteria (required vs optional)
- Input params and output params

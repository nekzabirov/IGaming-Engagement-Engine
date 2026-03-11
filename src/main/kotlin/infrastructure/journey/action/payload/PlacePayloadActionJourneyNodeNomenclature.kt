package com.nekgambling.infrastructure.journey.action.payload

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object PlacePayloadActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<PlacePayloadActionJourneyNode>() {
    override val nodeType: KClass<PlacePayloadActionJourneyNode> = PlacePayloadActionJourneyNode::class

    override val identity: String = "placePayloadAction"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun toAssetsMap(node: PlacePayloadActionJourneyNode): Map<String, Any> = mapOf(
        "key" to node.key,
        "value" to node.value,
    )

    override fun fromAssetsMap(map: Map<String, Any>): PlacePayloadActionJourneyNode = PlacePayloadActionJourneyNode(
        key = map["key"] as String,
        value = map["value"]!!,
    )
}

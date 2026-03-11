package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object SegmentTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<SegmentTriggerJourneyNode> {
    override val nodeType: KClass<SegmentTriggerJourneyNode> = SegmentTriggerJourneyNode::class

    override val identity: String = "segmentTrigger"

    override fun inputParams(): Set<String> =
        setOf("triggerName", "segment")

    override fun outputParams(): Set<String> =
        emptySet()

    override fun toAssetsMap(node: SegmentTriggerJourneyNode): Map<String, Any> = buildMap {
        put("type", node.type.name)
        node.segment?.let { put("segment", it) }
    }

    override fun fromAssetsMap(map: Map<String, Any>): SegmentTriggerJourneyNode = SegmentTriggerJourneyNode(
        type = SegmentTriggerJourneyNode.Type.valueOf(map["type"] as String),
        segment = map["segment"] as? String,
    )
}

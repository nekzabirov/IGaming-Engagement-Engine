package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNode
import kotlin.reflect.KClass

object SegmentTriggerNodeMapper : JourneyNodeMapper<SegmentTriggerJourneyNode> {
    override val type: String = "segmentTrigger"
    override val nodeType: KClass<SegmentTriggerJourneyNode> = SegmentTriggerJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): SegmentTriggerJourneyNode = SegmentTriggerJourneyNode(
        id = entity.id.value,
        type = SegmentTriggerJourneyNode.Type.valueOf(entity.segmentType!!),
        segment = entity.segment,
        next = next,
    )

    override fun applyToEntity(node: SegmentTriggerJourneyNode, entity: JourneyNodeEntity) {
        entity.segmentType = node.type.name
        entity.segment = node.segment
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNode
import kotlin.reflect.KClass

object SegmentTriggerNodeMapper : IJourneyNodeMapper<SegmentTriggerJourneyNode> {
    override val nodeType: KClass<SegmentTriggerJourneyNode> = SegmentTriggerJourneyNode::class
    override val identity: String = "segmentTrigger"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): SegmentTriggerJourneyNode {
        return SegmentTriggerJourneyNode(
            id = entity.id.value,
            type = enumValueOf<SegmentTriggerJourneyNode.Type>(entity.segmentType!!),
            segment = entity.segment,
            next = resolveNode(entity.next),
        )
    }
}

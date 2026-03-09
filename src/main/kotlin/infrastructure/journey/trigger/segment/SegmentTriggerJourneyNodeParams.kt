package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object SegmentTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<SegmentTriggerJourneyNode> {
    override val nodeType: KClass<SegmentTriggerJourneyNode> = SegmentTriggerJourneyNode::class

    override fun inputParams(): Set<String> =
        setOf("segment")

    override fun outputParams(): Set<String> =
        emptySet()
}

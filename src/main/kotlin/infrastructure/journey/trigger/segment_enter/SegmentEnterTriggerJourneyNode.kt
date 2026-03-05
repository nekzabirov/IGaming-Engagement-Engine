package com.nekgambling.infrastructure.journey.trigger.segment_enter

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class SegmentEnterTriggerJourneyNode(
    val segmentIdentity: String? = null,

    override val prev: IJourneyNode? = null,
    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode {
    override fun inputParams(): Set<String> = setOf("segmentIdentity")

    override fun outputParams(): Set<String> = setOf("segmentIdentity")
}
package com.nekgambling.infrastructure.journey.trigger.segment_enter

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class SegmentEnterTriggerJourneyNode(
    val segmentIdentity: String? = null,

    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(next = _next) {
    override fun inputParams(): Set<String> = setOf("segmentIdentity")

    override fun outputParams(): Set<String> = setOf("segmentIdentity")
}

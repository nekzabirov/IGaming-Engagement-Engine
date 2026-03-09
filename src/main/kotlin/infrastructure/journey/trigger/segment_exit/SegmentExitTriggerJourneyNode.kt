package com.nekgambling.infrastructure.journey.trigger.segment_exit

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class SegmentExitTriggerJourneyNode(
    private val _id: Long = Long.MIN_VALUE,
    val segmentIdentity: String? = null,

    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = _id, next = _next) {
    override fun inputParams(): Set<String> = setOf("segmentIdentity")

    override fun outputParams(): Set<String> = setOf("segmentIdentity")
}

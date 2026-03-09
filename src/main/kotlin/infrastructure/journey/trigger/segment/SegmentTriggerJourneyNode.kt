package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class SegmentTriggerJourneyNode(
    private val _id: Long = Long.MIN_VALUE,
    val type: Type,
    val segment: String? = null,

    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = _id, next = _next) {

    enum class Type { ENTER, EXIT }

    override fun inputParams(): Set<String> = setOf("segment")

    override fun outputParams(): Set<String> = emptySet()
}

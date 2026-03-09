package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class SegmentTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val type: Type,
    val segment: String? = null,

    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {

    enum class Type { ENTER, EXIT }

    companion object {
        const val TRIGGER_NAME = "segment"
    }
}

package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("segmentTrigger")
data class SegmentTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val type: Type,
    val segment: String? = null,

    @Polymorphic override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {

    @Serializable
    enum class Type { ENTER, EXIT }

    companion object {
        const val TRIGGER_NAME = "segment"
    }
}

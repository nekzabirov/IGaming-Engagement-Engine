package com.nekgambling.infrastructure.journey.action.payload

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("placePayload")
data class PlacePayloadActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    val key: String,
    @Contextual val value: Any,
) : IActionJourneyNode(id, next)

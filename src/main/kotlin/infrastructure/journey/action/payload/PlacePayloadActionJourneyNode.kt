package com.nekgambling.infrastructure.journey.action.payload

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode

data class PlacePayloadActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val key: String,
    val value: Any,
) : IActionJourneyNode(id, next)

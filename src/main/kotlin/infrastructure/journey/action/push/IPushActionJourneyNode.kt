package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode

sealed class IPushActionJourneyNode(
    id: Long = Long.MIN_VALUE,
    next: IJourneyNode? = null,
    open val templateId: String,
    open val placeHolders: Map<String, Any> = mapOf(),
) : IActionJourneyNode(id, next)
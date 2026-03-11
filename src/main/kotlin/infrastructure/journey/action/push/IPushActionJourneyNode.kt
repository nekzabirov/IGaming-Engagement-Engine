package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class IPushActionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
    @Transient open val templateId: String = "",
    @Transient open val placeHolders: Map<String, Any> = mapOf(),
) : IActionJourneyNode(id, next)

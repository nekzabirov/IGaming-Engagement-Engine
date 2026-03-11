package com.nekgambling.infrastructure.journey.action

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class IActionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
) : IJourneyNode(id, next)

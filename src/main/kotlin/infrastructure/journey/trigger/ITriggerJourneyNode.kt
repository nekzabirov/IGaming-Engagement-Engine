package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class ITriggerJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
) : IJourneyNode(id = id, next = next)

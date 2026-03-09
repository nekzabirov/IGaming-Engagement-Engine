package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class ITriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IJourneyNode(id = id, next = next)

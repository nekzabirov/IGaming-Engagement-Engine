package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode

abstract class ITriggerJourneyNode(
    id: Long = Long.MIN_VALUE,
    next: IJourneyNode? = null,
) : IJourneyNode(id = id, next = next)

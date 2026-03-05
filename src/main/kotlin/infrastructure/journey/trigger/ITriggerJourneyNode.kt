package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode

abstract class ITriggerJourneyNode(
    prev: IJourneyNode? = null,
    next: IJourneyNode? = null,
) : IJourneyNode(prev = prev, next = next)

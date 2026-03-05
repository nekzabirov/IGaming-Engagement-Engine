package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode

abstract class ITriggerJourneyNode(
    next: IJourneyNode? = null,
) : IJourneyNode(next = next)

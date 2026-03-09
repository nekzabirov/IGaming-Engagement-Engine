package com.nekgambling.infrastructure.journey.action

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class IActionJourneyNode(id: Long = Long.MIN_VALUE, next: IJourneyNode? = null) : IJourneyNode(id, next)
package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.trigger.model.Trigger

data class TriggerJourneyNode(
    val trigger: Trigger,

    override val prev: IJourneyNode?,
    override val next: IJourneyNode?
) : IJourneyNode
package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode

interface ITriggerJourneyNode : IJourneyNode {
    override val prev: IJourneyNode?
    override val next: IJourneyNode?
}
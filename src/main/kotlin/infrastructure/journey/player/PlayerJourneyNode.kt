package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class PlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    open val matchNode: IJourneyNode? = null,
    open val notMatchNode: IJourneyNode? = null,
) : IJourneyNode(id = id, next = matchNode)

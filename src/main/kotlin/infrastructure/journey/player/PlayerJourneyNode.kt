package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.model.journey.IJourneyNode

interface IPlayerDefinition

data class PlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val rule: IPlayerDefinition,
    val matchNode: IJourneyNode? = null,
    val notMatchNode: IJourneyNode? = null,
) : IJourneyNode(id = id, next = matchNode) {
    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}
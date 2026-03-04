package com.nekgambling.domain.journey.model

import com.nekgambling.domain.condition.model.Condition

data class ConditionJourneyNode(
    val condition: Condition,

    val matchNode: IJourneyNode?,
    val notMatchNode: IJourneyNode?,

    override val prev: IJourneyNode?,
) : IJourneyNode {
    override val next: IJourneyNode? = matchNode
}

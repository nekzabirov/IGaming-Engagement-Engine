package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.journey.model.IJourneyNode

data class ConditionJourneyNode(
    val condition: Condition,

    val matchNode: IJourneyNode?,
    val notMatchNode: IJourneyNode?,

    override val prev: IJourneyNode?,
) : IJourneyNode {
    override val next: IJourneyNode? = matchNode

    override fun requireParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}
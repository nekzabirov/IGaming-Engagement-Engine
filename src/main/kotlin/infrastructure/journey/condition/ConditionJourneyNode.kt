package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.journey.model.IJourneyNode

data class ConditionJourneyNode(
    val condition: Condition,

    val onMatch: IJourneyNode?,
    val onMismatch: IJourneyNode?,

    override val prev: IJourneyNode?,
) : IJourneyNode {
    override val next: IJourneyNode? = onMatch

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}
package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.journey.model.IJourneyNode

data class ConditionJourneyNode(
    val condition: Condition,

    val onMatch: IJourneyNode?,
    val onMismatch: IJourneyNode?,

    private val _prev: IJourneyNode? = null,
) : IJourneyNode(prev = _prev, next = onMatch) {

    init {
        var node = onMismatch
        while (node != null) {
            require(node !== this) { "Circular dependency detected in journey node chain (onMismatch)" }
            node = node.next
        }
    }

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}

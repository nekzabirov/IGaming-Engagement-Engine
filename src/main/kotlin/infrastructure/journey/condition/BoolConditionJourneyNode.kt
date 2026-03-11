package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode

data class BoolConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val expected: Boolean,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Any): Boolean {
        require(value is Boolean) { "Bool condition must be of type Boolean, got ${value.javaClass}" }
        return value == expected
    }
}

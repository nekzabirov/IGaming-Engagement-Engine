package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode

sealed class StringConditionJourneyNode(
    id: Long = Long.MIN_VALUE,
    inputKey: String,
    matchNode: IJourneyNode? = null,
    notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {

    protected abstract fun evaluate(value: String): Boolean

    override fun evaluate(value: Any): Boolean {
        require(value is String) { "String condition must be of type String, got ${value.javaClass}" }
        return evaluate(value)
    }
}

data class StringEqualConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val target: String,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : StringConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: String): Boolean {
        return value == target
    }
}

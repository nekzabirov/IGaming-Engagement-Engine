package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode

sealed class NumberConditionJourneyNode(
    id: Long = Long.MIN_VALUE,
    inputKey: String,
    matchNode: IJourneyNode? = null,
    notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {

    protected abstract fun evaluate(value: Number) : Boolean

    override fun evaluate(value: Any): Boolean {
        require(value is Number) { "Number condition must be of type ${value.javaClass}" }

        return evaluate(value)
    }
}

data class NumberInRangeConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val min: Number,
    val max: Number,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() in min.toDouble()..max.toDouble()
    }
}

data class NumberMoreThanConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val threshold: Number,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() > threshold.toDouble()
    }
}

data class NumberLessThanConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val threshold: Number,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() < threshold.toDouble()
    }
}

data class NumberEqualConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val target: Number,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() == target.toDouble()
    }
}
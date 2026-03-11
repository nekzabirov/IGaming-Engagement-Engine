package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class NumberConditionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val inputKey: String = "",
    @Transient override val matchNode: IJourneyNode? = null,
    @Transient override val notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {

    protected abstract fun evaluate(value: Number) : Boolean

    override fun evaluate(value: Any): Boolean {
        require(value is Number) { "Number condition must be of type ${value.javaClass}" }

        return evaluate(value)
    }
}

@Serializable
@SerialName("numberInRange")
data class NumberInRangeConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val min: Double,
    val max: Double,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() in min..max
    }
}

@Serializable
@SerialName("numberMoreThan")
data class NumberMoreThanConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val threshold: Double,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() > threshold
    }
}

@Serializable
@SerialName("numberLessThan")
data class NumberLessThanConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val threshold: Double,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() < threshold
    }
}

@Serializable
@SerialName("numberEqual")
data class NumberEqualConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val target: Double,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : NumberConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Number): Boolean {
        return value.toDouble() == target
    }
}

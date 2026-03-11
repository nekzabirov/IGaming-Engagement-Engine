package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class StringConditionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val inputKey: String = "",
    @Transient override val matchNode: IJourneyNode? = null,
    @Transient override val notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {

    protected abstract fun evaluate(value: String): Boolean

    override fun evaluate(value: Any): Boolean {
        require(value is String) { "String condition must be of type String, got ${value.javaClass}" }
        return evaluate(value)
    }
}

@Serializable
@SerialName("stringEqual")
data class StringEqualConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val target: String,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : StringConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: String): Boolean {
        return value == target
    }
}

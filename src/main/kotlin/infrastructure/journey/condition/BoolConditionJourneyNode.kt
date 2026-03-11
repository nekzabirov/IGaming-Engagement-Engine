package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("boolCondition")
data class BoolConditionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val inputKey: String,
    val expected: Boolean,
    @Polymorphic override val matchNode: IJourneyNode? = null,
    @Polymorphic override val notMatchNode: IJourneyNode? = null
) : IConditionJourneyNode(id, inputKey, matchNode, notMatchNode) {
    override fun evaluate(value: Any): Boolean {
        require(value is Boolean) { "Bool condition must be of type Boolean, got ${value.javaClass}" }
        return value == expected
    }
}

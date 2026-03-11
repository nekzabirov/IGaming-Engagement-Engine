package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class IConditionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient open val inputKey: String = "",
    @Transient open val matchNode: IJourneyNode? = null,
    @Transient open val notMatchNode: IJourneyNode? = null) : IJourneyNode(id, matchNode) {

    abstract fun evaluate(value: Any) : Boolean

}

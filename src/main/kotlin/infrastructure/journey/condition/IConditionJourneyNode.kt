package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class IConditionJourneyNode(
    id: Long = Long.MIN_VALUE,
    open val inputKey: String,
    open val matchNode: IJourneyNode? = null,
    open val notMatchNode: IJourneyNode? = null) : IJourneyNode(id, matchNode) {

    abstract fun evaluate(value: Any) : Boolean

}
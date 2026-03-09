package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode

abstract class IAmountExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    private val inputKey: String = "",
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val OUTPUT_KEY = "amount"
    }

    protected abstract fun calculate(amount: Long): Long

    override fun extract(inputs: Map<String, Any>): Pair<String, Any> {
        require(inputs.containsKey(inputKey)) {
            "Parameters are not defined for $inputKey"
        }

        val param: Any = inputs.getValue(inputKey)

        require(param is Long) { "Parameter $inputKey is not a long" }

        return Pair(OUTPUT_KEY, calculate(param))
    }

}
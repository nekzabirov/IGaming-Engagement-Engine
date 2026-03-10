package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode

abstract class IAmountExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    open val inputKey: String = "",
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val OUTPUT_KEY = "amount"
    }

    abstract fun calculate(amount: Long): Long
}

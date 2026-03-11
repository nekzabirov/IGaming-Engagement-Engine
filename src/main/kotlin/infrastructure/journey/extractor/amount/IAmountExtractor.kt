package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class IAmountExtractor(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
    @Transient open val inputKey: String = "",
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val OUTPUT_KEY = "amount"
    }

    abstract fun calculate(amount: Long): Long
}

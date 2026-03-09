package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.model.journey.IJourneyNode

data class PercentageAmountExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val inputKey: String = "",
    val percentage: Int,
    val maxAmount: Long? = null,
) : IAmountExtractor(id, next, inputKey) {

    override fun calculate(amount: Long): Long {
        val result =  (amount * percentage / 100)

        if (maxAmount != null && result > maxAmount) {
            return maxAmount
        }

        return result
    }
}

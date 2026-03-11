package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("percentageAmount")
data class PercentageAmountExtractor(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    override val inputKey: String = "",
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

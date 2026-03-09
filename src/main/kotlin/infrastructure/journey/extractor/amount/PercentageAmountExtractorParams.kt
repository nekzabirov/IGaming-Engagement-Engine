package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.strategy.JourneyNodeParams
import kotlin.reflect.KClass

object PercentageAmountExtractorParams : JourneyNodeParams<PercentageAmountExtractor> {
    override val nodeType: KClass<PercentageAmountExtractor> = PercentageAmountExtractor::class

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> =
        setOf(IAmountExtractor.OUTPUT_KEY)
}

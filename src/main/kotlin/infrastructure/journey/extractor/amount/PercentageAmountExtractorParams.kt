package com.nekgambling.infrastructure.journey.extractor.amount

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object PercentageAmountExtractorParams : JourneyNodeNomenclature<PercentageAmountExtractor> {
    override val nodeType: KClass<PercentageAmountExtractor> = PercentageAmountExtractor::class

    override val identity: String = "percentageAmountExtractor"

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> =
        setOf(IAmountExtractor.OUTPUT_KEY)
}

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

    override fun toAssetsMap(node: PercentageAmountExtractor): Map<String, Any> = buildMap {
        put("inputKey", node.inputKey)
        put("percentage", node.percentage)
        node.maxAmount?.let { put("maxAmount", it) }
    }

    override fun fromAssetsMap(map: Map<String, Any>): PercentageAmountExtractor = PercentageAmountExtractor(
        inputKey = map["inputKey"] as? String ?: "",
        percentage = (map["percentage"] as Number).toInt(),
        maxAmount = (map["maxAmount"] as? Number)?.toLong(),
    )
}

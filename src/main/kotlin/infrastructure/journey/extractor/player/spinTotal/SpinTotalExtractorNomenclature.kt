package com.nekgambling.infrastructure.journey.extractor.player.spinTotal

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object SpinTotalExtractorNomenclature : JourneyNodeNomenclature<SpinTotalExtractor> {
    override val nodeType: KClass<SpinTotalExtractor> = SpinTotalExtractor::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:placeAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:settleAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:realPlaceAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:realSettleAmount",
    )
}

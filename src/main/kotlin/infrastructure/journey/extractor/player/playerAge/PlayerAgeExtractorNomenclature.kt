package com.nekgambling.infrastructure.journey.extractor.player.playerAge

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object PlayerAgeExtractorNomenclature : JourneyNodeNomenclature<PlayerAgeExtractor> {
    override val nodeType: KClass<PlayerAgeExtractor> = PlayerAgeExtractor::class

    override val identity: String = "playerAgeExtractor"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:age",
    )
}

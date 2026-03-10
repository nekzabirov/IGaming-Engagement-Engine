package com.nekgambling.infrastructure.journey.extractor.player.playerGgr

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object PlayerGgrExtractorNomenclature : JourneyNodeNomenclature<PlayerGgrExtractor> {
    override val nodeType: KClass<PlayerGgrExtractor> = PlayerGgrExtractor::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:ggr",
        "${IPlayerExtractorJourneyNode.PREFIX}:ngr",
    )
}

package com.nekgambling.infrastructure.journey.extractor.player.playerAge

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object PlayerAgeExtractorNomenclature : JourneyNodeNomenclature<PlayerAgeExtractor> {
    override val nodeType: KClass<PlayerAgeExtractor> = PlayerAgeExtractor::class

    override val identity: String = "playerAgeExtractor"

    override val category: NodeCategory = NodeCategory.EXTRACTOR

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:age",
    )

    override fun assetsSchema(): List<AssetParamDescriptor> = emptyList()

    override fun toAssetsMap(node: PlayerAgeExtractor): Map<String, Any> = emptyMap()

    override fun fromAssetsMap(map: Map<String, Any>): PlayerAgeExtractor = PlayerAgeExtractor()
}

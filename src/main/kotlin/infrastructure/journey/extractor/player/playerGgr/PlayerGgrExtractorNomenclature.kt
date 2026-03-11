package com.nekgambling.infrastructure.journey.extractor.player.playerGgr

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory
import com.nekgambling.domain.strategy.ParamType
import com.nekgambling.domain.strategy.dateParamValueSubtypes
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object PlayerGgrExtractorNomenclature : JourneyNodeNomenclature<PlayerGgrExtractor> {
    override val nodeType: KClass<PlayerGgrExtractor> = PlayerGgrExtractor::class

    override val identity: String = "playerGgrExtractor"

    override val category: NodeCategory = NodeCategory.EXTRACTOR

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:ggr",
        "${IPlayerExtractorJourneyNode.PREFIX}:ngr",
    )

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(
            name = "date", type = ParamType.OBJECT, required = true,
            subtypes = dateParamValueSubtypes(),
        ),
    )

    @Suppress("UNCHECKED_CAST")
    override fun toAssetsMap(node: PlayerGgrExtractor): Map<String, Any> = mapOf(
        "date" to node.date.toMap()
    )

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): PlayerGgrExtractor = PlayerGgrExtractor(
        date = DateParamValue.fromMap(map["date"] as Map<String, Any>),
    )
}

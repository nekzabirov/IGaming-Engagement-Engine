package com.nekgambling.infrastructure.journey.extractor.player.spinTotal

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory
import com.nekgambling.domain.strategy.ParamType
import com.nekgambling.domain.strategy.dateParamValueSubtypes
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object SpinTotalExtractorNomenclature : JourneyNodeNomenclature<SpinTotalExtractor> {
    override val nodeType: KClass<SpinTotalExtractor> = SpinTotalExtractor::class

    override val identity: String = "spinTotalExtractor"

    override val category: NodeCategory = NodeCategory.EXTRACTOR

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:placeAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:settleAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:realPlaceAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:realSettleAmount",
    )

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(
            name = "date", type = ParamType.OBJECT, required = true,
            subtypes = dateParamValueSubtypes(),
        ),
    )

    @Suppress("UNCHECKED_CAST")
    override fun toAssetsMap(node: SpinTotalExtractor): Map<String, Any> = mapOf(
        "date" to node.date.toMap()
    )

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): SpinTotalExtractor = SpinTotalExtractor(
        date = DateParamValue.fromMap(map["date"] as Map<String, Any>),
    )
}

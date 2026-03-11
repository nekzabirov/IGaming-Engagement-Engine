package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory
import com.nekgambling.domain.strategy.ParamType
import com.nekgambling.domain.strategy.dateParamValueSubtypes
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object InvoiceTotalExtractorNomenclature : JourneyNodeNomenclature<InvoiceTotalExtractor> {
    override val nodeType: KClass<InvoiceTotalExtractor> = InvoiceTotalExtractor::class

    override val identity: String = "invoiceTotalExtractor"

    override val category: NodeCategory = NodeCategory.EXTRACTOR

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:depositAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:withdrawAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:depositCount",
        "${IPlayerExtractorJourneyNode.PREFIX}:withdrawCount",
        "${IPlayerExtractorJourneyNode.PREFIX}:taxAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:feesAmount",
    )

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(
            name = "date", type = ParamType.OBJECT, required = true,
            subtypes = dateParamValueSubtypes(),
        ),
    )

    @Suppress("UNCHECKED_CAST")
    override fun toAssetsMap(node: InvoiceTotalExtractor): Map<String, Any> = mapOf(
        "date" to node.date.toMap()
    )

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): InvoiceTotalExtractor = InvoiceTotalExtractor(
        date = DateParamValue.fromMap(map["date"] as Map<String, Any>),
    )

    override fun withLinks(node: InvoiceTotalExtractor, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): InvoiceTotalExtractor =
        node.copy(next = next)
}

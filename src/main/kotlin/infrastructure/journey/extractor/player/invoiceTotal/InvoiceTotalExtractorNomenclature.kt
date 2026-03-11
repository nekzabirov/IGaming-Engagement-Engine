package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object InvoiceTotalExtractorNomenclature : JourneyNodeNomenclature<InvoiceTotalExtractor> {
    override val nodeType: KClass<InvoiceTotalExtractor> = InvoiceTotalExtractor::class

    override val identity: String = "invoiceTotalExtractor"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:depositAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:withdrawAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:depositCount",
        "${IPlayerExtractorJourneyNode.PREFIX}:withdrawCount",
        "${IPlayerExtractorJourneyNode.PREFIX}:taxAmount",
        "${IPlayerExtractorJourneyNode.PREFIX}:feesAmount",
    )

    @Suppress("UNCHECKED_CAST")
    override fun toAssetsMap(node: InvoiceTotalExtractor): Map<String, Any> = mapOf(
        "date" to node.date.toMap()
    )

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): InvoiceTotalExtractor = InvoiceTotalExtractor(
        date = DateParamValue.fromMap(map["date"] as Map<String, Any>),
    )
}

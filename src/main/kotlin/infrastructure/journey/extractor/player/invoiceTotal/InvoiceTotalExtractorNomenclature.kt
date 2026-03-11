package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
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
}

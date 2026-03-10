package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerInvoiceTotalQuery
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode.Companion.buildOutput
import kotlin.reflect.KClass

class InvoiceTotalExtractorProcess(
    private val queryBus: QueryBus,
) : ExtractorJourneyNodeProcess<InvoiceTotalExtractor>() {

    override val nodeType: KClass<InvoiceTotalExtractor> = InvoiceTotalExtractor::class

    override suspend fun extract(
        playerId: String,
        node: InvoiceTotalExtractor,
        payload: Map<String, Any>,
    ): Map<String, Any> {
        val total = queryBus.execute(GetPlayerInvoiceTotalQuery(playerId, node.date.toPeriod()))

        return buildOutput(
            "depositAmount" to total.depositAmount,
            "withdrawAmount" to total.withdrawAmount,
            "depositCount" to total.depositCount,
            "withdrawCount" to total.withdrawCount,
            "taxAmount" to total.taxAmount,
            "feesAmount" to total.feesAmount,
        )
    }
}

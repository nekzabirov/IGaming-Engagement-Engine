package com.nekgambling.infrastructure.journey.player.invoiceTotal

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerInvoiceTotalQuery
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.player.IPlayerJourneyNodeProcess
import kotlin.reflect.KClass

class InvoiceTotalPlayerJourneyNodeProcess(
    private val queryBus: QueryBus,
) : IPlayerJourneyNodeProcess<InvoiceTotalPlayerJourneyNode> {

    override val nodeType: KClass<InvoiceTotalPlayerJourneyNode> = InvoiceTotalPlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: InvoiceTotalPlayerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response {
        val total = queryBus.execute(GetPlayerInvoiceTotalQuery(playerId, node.date.toPeriod()))

        val matched = (node.depositCount == null || node.depositCount.check(total.depositCount))
                && (node.withdrawCount == null || node.withdrawCount.check(total.withdrawCount))
                && (node.depositAmount == null || node.depositAmount.check(total.depositAmount))
                && (node.withdrawAmount == null || node.withdrawAmount.check(total.withdrawAmount))
                && (node.taxAmount == null || node.taxAmount.check(total.taxAmount))
                && (node.feesAmount == null || node.feesAmount.check(total.feesAmount))

        return JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }
}

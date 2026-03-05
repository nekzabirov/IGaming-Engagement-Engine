package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class InvoiceTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<InvoiceTriggerJourneyNode> {
    override val node: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: InvoiceTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val typeStr = payload["type"] as? String ?: error("Missing required payload param: type")
        val type = runCatching { PlayerInvoice.Type.valueOf(typeStr) }.getOrElse { error("Invalid invoice type: $typeStr") }
        val statusStr = payload["status"] as? String ?: error("Missing required payload param: status")
        val status = runCatching { PlayerInvoice.Status.valueOf(statusStr) }.getOrElse { error("Invalid invoice status: $statusStr") }
        val currencyStr = payload["currency"] as? String ?: error("Missing required payload param: currency")
        val transactionCurrency = Currency(currencyStr)
        val amount = (payload["amount"] as? Number)?.toLong() ?: error("Missing required payload param: amount")
        val transactionAmount = (payload["transactionAmount"] as? Number)?.toLong() ?: error("Missing required payload param: transactionAmount")
        val taxAmount = (payload["taxAmount"] as? Number)?.toLong() ?: error("Missing required payload param: taxAmount")
        val feeAmount = (payload["feeAmount"] as? Number)?.toLong() ?: error("Missing required payload param: feeAmount")

        val matched = type == node.type
                && status == node.status
                && (node.currency == null || transactionCurrency == node.currency)
                && (node.amount == null || node.amount.check(amount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                "currency" to currencyStr,
                "amount" to amount.toString(),
                "transactionAmount" to transactionAmount.toString(),
                "taxAmount" to taxAmount.toString(),
                "fees" to feeAmount.toString(),
            ),
        )
    }
}

package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class InvoiceTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<InvoiceTriggerJourneyNode> {
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: InvoiceTriggerJourneyNode,
        payload: Payload,
    ): JourneyNodeProcess.Response? {
        val triggerName = payload["triggerName"] as? String ?: return null
        if (triggerName != InvoiceTriggerJourneyNode.TRIGGER_NAME) return null

        val typeStr = payload["invoiceType"] as? String ?: error("Missing required payload param: invoiceType")
        val type = runCatching { PlayerInvoice.Type.valueOf(typeStr) }.getOrElse { error("Invalid invoice type: $typeStr") }
        val statusStr = payload["invoiceStatus"] as? String ?: error("Missing required payload param: invoiceStatus")
        val status = runCatching { PlayerInvoice.Status.valueOf(statusStr) }.getOrElse { error("Invalid invoice status: $statusStr") }
        val currencyStr = payload["invoiceCurrency"] as? String ?: error("Missing required payload param: invoiceCurrency")
        val invoiceCurrency = Currency(currencyStr)
        val amount = (payload["invoiceAmount"] as? Number)?.toLong() ?: error("Missing required payload param: invoiceAmount")
        val transactionAmount = (payload["invoiceTransactionAmount"] as? Number)?.toLong() ?: error("Missing required payload param: invoiceTransactionAmount")
        val taxAmount = (payload["invoiceTaxAmount"] as? Number)?.toLong() ?: error("Missing required payload param: invoiceTaxAmount")
        val feeAmount = (payload["invoiceFeeAmount"] as? Number)?.toLong() ?: error("Missing required payload param: invoiceFeeAmount")

        val matched = type == node.invoiceType
                && status == node.invoiceStatus
                && (node.invoiceCurrency == null || invoiceCurrency == node.invoiceCurrency)
                && (node.invoiceAmount == null || node.invoiceAmount.matches(amount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                "invoice:currency" to currencyStr,
                "invoice:amount" to amount.toString(),
                "invoice:transactionAmount" to transactionAmount.toString(),
                "invoice:taxAmount" to taxAmount.toString(),
                "invoice:feeAmount" to feeAmount.toString(),
            ),
        )
    }
}

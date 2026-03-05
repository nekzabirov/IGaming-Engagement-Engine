package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class InvoiceTriggerJourneyNode(
    val invoiceType: PlayerInvoice.Type,
    val invoiceStatus: PlayerInvoice.Status,
    val invoiceCurrency: Currency? = null,
    val invoiceAmount: NumberParamValue? = null,

    override val prev: IJourneyNode? = null,
    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode {
    override fun inputParams(): Set<String> = setOf("invoiceType", "invoiceStatus", "invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")

    override fun outputParams(): Set<String> = setOf("invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")
}
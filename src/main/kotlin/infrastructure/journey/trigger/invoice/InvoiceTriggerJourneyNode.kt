package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class InvoiceTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val invoiceType: PlayerInvoice.Type,
    val invoiceStatus: PlayerInvoice.Status,
    val invoiceCurrency: Currency? = null,
    val invoiceAmount: NumberParamValue? = null,

    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {
    override fun inputParams(): Set<String> = setOf("invoiceType", "invoiceStatus", "invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")

    override fun outputParams(): Set<String> = setOf("invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")
}

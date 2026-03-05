package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class InvoiceTriggerJourneyNode(
    val type: PlayerInvoice.Type,
    val status: PlayerInvoice.Status,
    val currency: Currency? = null,
    val amount: NumberParamValue? = null,

    override val prev: IJourneyNode? = null,
    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode {
    override fun requireParams(): Set<String> = setOf("type", "status", "currency", "amount", "transactionAmount", "taxAmount", "feeAmount")

    override fun outputParams(): Set<String> = setOf("currency", "amount", "transactionAmount", "taxAmount", "fees")
}
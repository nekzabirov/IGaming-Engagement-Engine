package com.nekgambling.infrastructure.trigger

import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.domain.trigger.model.ITriggerRulePayload
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("invoice")
data class InvoiceTriggerRule(
    val type: PlayerInvoice.Type,
    val status: PlayerInvoice.Status,
    val currency: Currency? = null,
    val amount: NumberParamValue? = null,
) : ITriggerRule<InvoiceTriggerRule.Payload> {

    data class Payload(
        val type: PlayerInvoice.Type,
        val status: PlayerInvoice.Status,
        val transactionCurrency: Currency,
        val amount: Long,
        var transactionAmount: Long,
        var taxAmount: Long,
        var feeAmount: Long,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = mapOf(
            "currency" to transactionCurrency.toString(),
            "amount" to amount.toString(),
            "transactionAmount" to transactionAmount.toString(),
            "taxAmount" to taxAmount.toString(),
            "fees" to feeAmount.toString(),
        )
    }

    override fun evaluate(payload: Payload): Boolean =
        payload.type == type
                && payload.status == status
                && (currency == null || payload.transactionCurrency == currency)
                && (amount == null || amount.check(payload.amount))
}

package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("invoiceTrigger")
data class InvoiceTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val invoiceType: PlayerInvoice.Type,
    val invoiceStatus: PlayerInvoice.Status,
    val invoiceCurrency: Currency? = null,
    val invoiceAmount: NumberParamValue? = null,

    @Polymorphic override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {
    companion object {
        const val TRIGGER_NAME = "invoice"
    }
}

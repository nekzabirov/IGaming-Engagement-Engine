package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.strategy.JourneyNodeParams
import kotlin.reflect.KClass

object InvoiceTriggerJourneyNodeParams : JourneyNodeParams<InvoiceTriggerJourneyNode> {
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override fun inputParams(): Set<String> =
        setOf("invoiceType", "invoiceStatus", "invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")

    override fun outputParams(): Set<String> =
        setOf("invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")
}

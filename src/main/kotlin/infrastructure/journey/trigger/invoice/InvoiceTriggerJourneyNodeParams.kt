package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object InvoiceTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<InvoiceTriggerJourneyNode> {
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override val identity: String = "invoiceTrigger"

    override fun inputParams(): Set<String> =
        setOf("triggerName", "invoiceType", "invoiceStatus", "invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")

    override fun outputParams(): Set<String> =
        setOf("invoice:currency", "invoice:amount", "invoice:transactionAmount", "invoice:taxAmount", "invoice:feeAmount")
}

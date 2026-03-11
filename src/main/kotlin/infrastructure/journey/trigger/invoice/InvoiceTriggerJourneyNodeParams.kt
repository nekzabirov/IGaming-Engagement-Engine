package com.nekgambling.infrastructure.journey.trigger.invoice

import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.*
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.asset.NumberParamValue
import kotlin.reflect.KClass

object InvoiceTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<InvoiceTriggerJourneyNode> {
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override val identity: String = "invoiceTrigger"

    override val category: NodeCategory = NodeCategory.TRIGGER

    override fun inputParams(): Set<String> =
        setOf("triggerName", "invoiceType", "invoiceStatus", "invoiceCurrency", "invoiceAmount", "invoiceTransactionAmount", "invoiceTaxAmount", "invoiceFeeAmount")

    override fun outputParams(): Set<String> =
        setOf("invoice:currency", "invoice:amount", "invoice:transactionAmount", "invoice:taxAmount", "invoice:feeAmount")

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(
            name = "invoiceType", type = ParamType.ENUM, required = true,
            options = PlayerInvoice.Type.entries.map { it.name },
        ),
        AssetParamDescriptor(
            name = "invoiceStatus", type = ParamType.ENUM, required = true,
            options = PlayerInvoice.Status.entries.map { it.name },
        ),
        AssetParamDescriptor(name = "invoiceCurrency", type = ParamType.CURRENCY, required = false),
        AssetParamDescriptor(
            name = "invoiceAmount", type = ParamType.OBJECT, required = false,
            subtypes = numberParamValueSubtypes(),
        ),
    )

    override fun toAssetsMap(node: InvoiceTriggerJourneyNode): Map<String, Any> = buildMap {
        put("invoiceType", node.invoiceType.name)
        put("invoiceStatus", node.invoiceStatus.name)
        node.invoiceCurrency?.let { put("invoiceCurrency", it.code) }
        node.invoiceAmount?.let { put("invoiceAmount", it.toMap()) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): InvoiceTriggerJourneyNode = InvoiceTriggerJourneyNode(
        invoiceType = PlayerInvoice.Type.valueOf(map["invoiceType"] as String),
        invoiceStatus = PlayerInvoice.Status.valueOf(map["invoiceStatus"] as String),
        invoiceCurrency = (map["invoiceCurrency"] as? String)?.let { Currency(it) },
        invoiceAmount = (map["invoiceAmount"] as? Map<String, Any>)?.let { NumberParamValue.fromMap(it) },
    )

    override fun withLinks(node: InvoiceTriggerJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): InvoiceTriggerJourneyNode =
        node.copy(next = next)
}

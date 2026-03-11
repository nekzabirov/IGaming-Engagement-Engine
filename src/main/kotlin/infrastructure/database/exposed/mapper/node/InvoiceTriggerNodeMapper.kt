package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object InvoiceTriggerNodeMapper : JourneyNodeMapper<InvoiceTriggerJourneyNode> {
    override val type: String = "invoiceTrigger"
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): InvoiceTriggerJourneyNode = InvoiceTriggerJourneyNode(
        id = entity.id.value,
        invoiceType = PlayerInvoice.Type.valueOf(entity.invoiceType!!),
        invoiceStatus = PlayerInvoice.Status.valueOf(entity.invoiceStatus!!),
        invoiceCurrency = entity.currency?.let { Currency(it) },
        invoiceAmount = entity.invoiceAmount?.let {
            Json.decodeFromString(NumberParamValue.serializer(), it)
        },
        next = next,
    )

    override fun applyToEntity(node: InvoiceTriggerJourneyNode, entity: JourneyNodeEntity) {
        entity.invoiceType = node.invoiceType.name
        entity.invoiceStatus = node.invoiceStatus.name
        entity.currency = node.invoiceCurrency?.code
        entity.invoiceAmount = node.invoiceAmount?.let {
            Json.encodeToString(NumberParamValue.serializer(), it)
        }
    }
}

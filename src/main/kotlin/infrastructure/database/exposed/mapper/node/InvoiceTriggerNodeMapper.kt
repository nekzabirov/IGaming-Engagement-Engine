package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object InvoiceTriggerNodeMapper : IJourneyNodeMapper<InvoiceTriggerJourneyNode> {
    override val nodeType: KClass<InvoiceTriggerJourneyNode> = InvoiceTriggerJourneyNode::class
    override val identity: String = "invoiceTrigger"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): InvoiceTriggerJourneyNode {
        return InvoiceTriggerJourneyNode(
            id = entity.id.value,
            invoiceType = enumValueOf<PlayerInvoice.Type>(entity.invoiceType!!),
            invoiceStatus = enumValueOf<PlayerInvoice.Status>(entity.invoiceStatus!!),
            invoiceCurrency = entity.currency?.let { Currency(it) },
            invoiceAmount = entity.invoiceAmount?.let { Json.decodeFromString<NumberParamValue>(it) },
            next = resolveNode(entity.next),
        )
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal.InvoiceTotalExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object InvoiceTotalExtractorNodeMapper : JourneyNodeMapper<InvoiceTotalExtractor> {
    override val type: String = "invoiceTotalExtractor"
    override val nodeType: KClass<InvoiceTotalExtractor> = InvoiceTotalExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): InvoiceTotalExtractor = InvoiceTotalExtractor(
        id = entity.id.value,
        next = next,
        date = Json.decodeFromString(DateParamValue.serializer(), entity.dateParam!!),
    )

    override fun applyToEntity(node: InvoiceTotalExtractor, entity: JourneyNodeEntity) {
        entity.dateParam = Json.encodeToString(DateParamValue.serializer(), node.date)
    }
}

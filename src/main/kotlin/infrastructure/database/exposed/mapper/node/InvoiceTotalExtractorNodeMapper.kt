package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal.InvoiceTotalExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object InvoiceTotalExtractorNodeMapper : IJourneyNodeMapper<InvoiceTotalExtractor> {
    override val nodeType: KClass<InvoiceTotalExtractor> = InvoiceTotalExtractor::class
    override val identity: String = "invoiceTotal"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): InvoiceTotalExtractor {
        return InvoiceTotalExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
            date = Json.decodeFromString<DateParamValue>(entity.dateParam!!),
        )
    }
}

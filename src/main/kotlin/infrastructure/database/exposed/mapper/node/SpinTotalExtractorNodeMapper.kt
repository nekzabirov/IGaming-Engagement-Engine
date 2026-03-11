package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.spinTotal.SpinTotalExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object SpinTotalExtractorNodeMapper : IJourneyNodeMapper<SpinTotalExtractor> {
    override val nodeType: KClass<SpinTotalExtractor> = SpinTotalExtractor::class
    override val identity: String = "spinTotal"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): SpinTotalExtractor {
        return SpinTotalExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
            date = Json.decodeFromString<DateParamValue>(entity.dateParam!!),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: SpinTotalExtractor) {
        entity.dateParam = Json.encodeToString(DateParamValue.serializer(), node.date)
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.spinTotal.SpinTotalExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object SpinTotalExtractorNodeMapper : JourneyNodeMapper<SpinTotalExtractor> {
    override val type: String = "spinTotalExtractor"
    override val nodeType: KClass<SpinTotalExtractor> = SpinTotalExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): SpinTotalExtractor = SpinTotalExtractor(
        id = entity.id.value,
        next = next,
        date = Json.decodeFromString(DateParamValue.serializer(), entity.dateParam!!),
    )

    override fun applyToEntity(node: SpinTotalExtractor, entity: JourneyNodeEntity) {
        entity.dateParam = Json.encodeToString(DateParamValue.serializer(), node.date)
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerGgr.PlayerGgrExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object PlayerGgrExtractorNodeMapper : JourneyNodeMapper<PlayerGgrExtractor> {
    override val type: String = "playerGgrExtractor"
    override val nodeType: KClass<PlayerGgrExtractor> = PlayerGgrExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): PlayerGgrExtractor = PlayerGgrExtractor(
        id = entity.id.value,
        next = next,
        date = Json.decodeFromString(DateParamValue.serializer(), entity.dateParam!!),
    )

    override fun applyToEntity(node: PlayerGgrExtractor, entity: JourneyNodeEntity) {
        entity.dateParam = Json.encodeToString(DateParamValue.serializer(), node.date)
    }
}

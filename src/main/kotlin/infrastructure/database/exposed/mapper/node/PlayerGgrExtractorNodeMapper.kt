package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerGgr.PlayerGgrExtractor
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object PlayerGgrExtractorNodeMapper : IJourneyNodeMapper<PlayerGgrExtractor> {
    override val nodeType: KClass<PlayerGgrExtractor> = PlayerGgrExtractor::class
    override val identity: String = "playerGgr"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): PlayerGgrExtractor {
        return PlayerGgrExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
            date = Json.decodeFromString<DateParamValue>(entity.dateParam!!),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: PlayerGgrExtractor) {
        entity.dateParam = Json.encodeToString(DateParamValue.serializer(), node.date)
    }
}

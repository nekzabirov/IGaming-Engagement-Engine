package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerAge.PlayerAgeExtractor
import kotlin.reflect.KClass

object PlayerAgeExtractorNodeMapper : IJourneyNodeMapper<PlayerAgeExtractor> {
    override val nodeType: KClass<PlayerAgeExtractor> = PlayerAgeExtractor::class
    override val identity: String = "playerAge"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): PlayerAgeExtractor {
        return PlayerAgeExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: PlayerAgeExtractor) {
        // No data columns
    }
}

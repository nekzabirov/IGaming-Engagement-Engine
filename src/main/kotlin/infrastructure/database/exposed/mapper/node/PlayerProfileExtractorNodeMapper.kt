package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerProfile.PlayerProfileExtractor
import kotlin.reflect.KClass

object PlayerProfileExtractorNodeMapper : IJourneyNodeMapper<PlayerProfileExtractor> {
    override val nodeType: KClass<PlayerProfileExtractor> = PlayerProfileExtractor::class
    override val identity: String = "playerProfile"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): PlayerProfileExtractor {
        return PlayerProfileExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: PlayerProfileExtractor) {
        // No data columns
    }
}

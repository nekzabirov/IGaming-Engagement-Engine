package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerProfile.PlayerProfileExtractor
import kotlin.reflect.KClass

object PlayerProfileExtractorNodeMapper : JourneyNodeMapper<PlayerProfileExtractor> {
    override val type: String = "playerProfileExtractor"
    override val nodeType: KClass<PlayerProfileExtractor> = PlayerProfileExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): PlayerProfileExtractor = PlayerProfileExtractor(
        id = entity.id.value,
        next = next,
    )

    override fun applyToEntity(node: PlayerProfileExtractor, entity: JourneyNodeEntity) {
        // No additional fields
    }
}

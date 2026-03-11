package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.player.playerAge.PlayerAgeExtractor
import kotlin.reflect.KClass

object PlayerAgeExtractorNodeMapper : JourneyNodeMapper<PlayerAgeExtractor> {
    override val type: String = "playerAgeExtractor"
    override val nodeType: KClass<PlayerAgeExtractor> = PlayerAgeExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): PlayerAgeExtractor = PlayerAgeExtractor(
        id = entity.id.value,
        next = next,
    )

    override fun applyToEntity(node: PlayerAgeExtractor, entity: JourneyNodeEntity) {
        // No additional fields
    }
}

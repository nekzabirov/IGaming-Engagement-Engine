package com.nekgambling.infrastructure.journey.extractor.player.playerAge

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerAge")
data class PlayerAgeExtractor(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
) : IPlayerExtractorJourneyNode(id, next)

package com.nekgambling.infrastructure.journey.extractor.player.playerProfile

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerProfile")
data class PlayerProfileExtractor(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
) : IPlayerExtractorJourneyNode(id, next)

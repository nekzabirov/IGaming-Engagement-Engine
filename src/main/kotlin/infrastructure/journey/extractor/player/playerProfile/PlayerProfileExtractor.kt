package com.nekgambling.infrastructure.journey.extractor.player.playerProfile

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode

data class PlayerProfileExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IPlayerExtractorJourneyNode(id, next)

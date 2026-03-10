package com.nekgambling.infrastructure.journey.extractor.player.playerAge

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode

data class PlayerAgeExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IPlayerExtractorJourneyNode(id, next)

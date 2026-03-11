package com.nekgambling.infrastructure.journey.extractor.player.playerGgr

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode

data class PlayerGgrExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val date: DateParamValue,
) : IPlayerExtractorJourneyNode(id, next)

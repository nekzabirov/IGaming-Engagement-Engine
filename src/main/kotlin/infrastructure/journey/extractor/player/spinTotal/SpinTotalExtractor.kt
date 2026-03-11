package com.nekgambling.infrastructure.journey.extractor.player.spinTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode

data class SpinTotalExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val date: DateParamValue,
) : IPlayerExtractorJourneyNode(id, next)

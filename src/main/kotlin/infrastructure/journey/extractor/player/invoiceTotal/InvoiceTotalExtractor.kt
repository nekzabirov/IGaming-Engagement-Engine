package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode

data class InvoiceTotalExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val date: DateParamValue,
) : IPlayerExtractorJourneyNode(id, next)

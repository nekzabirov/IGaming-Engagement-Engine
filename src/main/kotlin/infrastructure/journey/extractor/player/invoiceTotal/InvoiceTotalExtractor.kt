package com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("invoiceTotal")
data class InvoiceTotalExtractor(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    val date: DateParamValue,
) : IPlayerExtractorJourneyNode(id, next)

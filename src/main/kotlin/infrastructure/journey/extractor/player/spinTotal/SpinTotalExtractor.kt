package com.nekgambling.infrastructure.journey.extractor.player.spinTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.asset.DateParamValue
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("spinTotal")
data class SpinTotalExtractor(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    val date: DateParamValue,
) : IPlayerExtractorJourneyNode(id, next)

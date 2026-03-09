package com.nekgambling.infrastructure.journey.extractor

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class IExtractorJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IJourneyNode(id = id, next = next) {

    abstract suspend fun extract(playerId: String, inputs: Map<String, Any>):  Map<String, Any>

}
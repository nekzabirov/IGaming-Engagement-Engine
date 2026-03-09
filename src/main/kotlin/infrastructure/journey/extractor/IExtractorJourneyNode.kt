package com.nekgambling.infrastructure.journey.extractor

import com.nekgambling.domain.model.journey.IJourneyNode

abstract class IExtractorJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IJourneyNode(id = id, next = next) {

    abstract fun extract(inputs: Map<String, Any>): Pair<String, Any>

}
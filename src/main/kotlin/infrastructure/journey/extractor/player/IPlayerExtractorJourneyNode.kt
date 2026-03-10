package com.nekgambling.infrastructure.journey.extractor.player

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode

abstract class IPlayerExtractorJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val PREFIX = "player"

        fun buildOutput(vararg pairs: Pair<String, Any?>): Map<String, Any> =
            pairs.mapNotNull { (key, value) ->
                value?.let { "$PREFIX:$key" to it }
            }.toMap()
    }
}

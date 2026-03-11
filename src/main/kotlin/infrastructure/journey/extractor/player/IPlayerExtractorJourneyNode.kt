package com.nekgambling.infrastructure.journey.extractor.player

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class IPlayerExtractorJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val PREFIX = "player"

        fun buildOutput(vararg pairs: Pair<String, Any?>): Payload =
            pairs.mapNotNull { (key, value) ->
                value?.let { "$PREFIX:$key" to it }
            }.toMap()
    }
}

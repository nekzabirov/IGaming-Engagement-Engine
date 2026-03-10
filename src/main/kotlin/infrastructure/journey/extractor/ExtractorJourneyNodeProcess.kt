package com.nekgambling.infrastructure.journey.extractor

import com.nekgambling.domain.strategy.JourneyNodeProcess

abstract class ExtractorJourneyNodeProcess<N : IExtractorJourneyNode> : JourneyNodeProcess<N> {

    abstract suspend fun extract(playerId: String, node: N, payload: Map<String, Any>): Map<String, Any>

    override suspend fun process(
        playerId: String,
        node: N,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response = JourneyNodeProcess.Response(
        nextNode = node.next,
        output = extract(playerId, node, payload),
    )
}

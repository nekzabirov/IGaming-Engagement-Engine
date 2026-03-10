package com.nekgambling.infrastructure.journey.extractor

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload

abstract class ExtractorJourneyNodeProcess<N : IExtractorJourneyNode> : JourneyNodeProcess<N> {

    abstract suspend fun extract(playerId: String, node: N, payload: Payload): Payload

    override suspend fun process(
        playerId: String,
        node: N,
        payload: Payload,
    ): JourneyNodeProcess.Response = JourneyNodeProcess.Response(
        nextNode = node.next,
        output = extract(playerId, node, payload),
    )
}

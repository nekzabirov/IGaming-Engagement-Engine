package com.nekgambling.domain.journey.strategy

import com.nekgambling.domain.journey.model.IJourneyNode

class JourneyNodeProcessResolver(
    private val processors: List<JourneyNodeProcess<*>>
) {

    suspend fun process(playerId: String, node: IJourneyNode, payload: Map<String, Any>): IJourneyNode? {
        val processor = processors.find { it.nodeType.isInstance(node) }
            ?: error("Cannot find processor for journey node ${node::class.simpleName}")

        return processUnchecked(processor, playerId, node, payload)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <N : IJourneyNode> processUnchecked(
        processor: JourneyNodeProcess<N>,
        playerId: String,
        node: IJourneyNode,
        payload: Map<String, Any>
    ): IJourneyNode? = processor.process(playerId, node as N, payload)

}

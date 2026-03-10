package com.nekgambling.application.resolver

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload

class JourneyNodeProcessResolver(private val processes: List<JourneyNodeProcess<*>>) {

    @Suppress("UNCHECKED_CAST")
    suspend fun process(playerId: String, node: IJourneyNode, payload: Payload): JourneyNodeProcess.Response? {
        val process = processes.find { it.nodeType.isInstance(node) }
            ?: return null
        return (process as JourneyNodeProcess<IJourneyNode>).process(playerId, node, payload)
    }
}

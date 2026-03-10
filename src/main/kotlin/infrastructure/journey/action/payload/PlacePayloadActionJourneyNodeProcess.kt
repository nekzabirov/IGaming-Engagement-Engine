package com.nekgambling.infrastructure.journey.action.payload

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.action.IActionJourneyNodeProcess
import kotlin.reflect.KClass

class PlacePayloadActionJourneyNodeProcess : IActionJourneyNodeProcess<PlacePayloadActionJourneyNode>() {
    override val nodeType: KClass<PlacePayloadActionJourneyNode> = PlacePayloadActionJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: PlacePayloadActionJourneyNode,
        payload: Payload
    ): JourneyNodeProcess.Response {
        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(node.key to node.value),
        )
    }
}

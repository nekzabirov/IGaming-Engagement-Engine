package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import kotlin.reflect.KClass

class TriggerJourneyNodeProcess : JourneyNodeProcess<TriggerJourneyNode> {
    override val node: KClass<TriggerJourneyNode> = TriggerJourneyNode::class

    override suspend fun process(playerId: String, node: TriggerJourneyNode, payload: Map<String, Any>): IJourneyNode? {
        return node.next
    }
}

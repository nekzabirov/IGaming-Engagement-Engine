package com.nekgambling.infrastructure.journey.trigger.segment

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class SegmentTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<SegmentTriggerJourneyNode> {
    override val nodeType: KClass<SegmentTriggerJourneyNode> = SegmentTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: SegmentTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val triggerName = payload["triggerName"] as? String ?: return null
        if (triggerName != SegmentTriggerJourneyNode.TRIGGER_NAME) return null

        val segmentIdentity = payload["segment"] as? String ?: error("Missing required payload param: segment")

        val matched = node.segment == null || segmentIdentity == node.segment

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = emptyMap(),
        )
    }
}

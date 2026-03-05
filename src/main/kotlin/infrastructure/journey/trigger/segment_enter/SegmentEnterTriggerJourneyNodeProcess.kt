package com.nekgambling.infrastructure.journey.trigger.segment_enter

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.segment_enter.SegmentEnterTriggerJourneyNode
import kotlin.reflect.KClass

class SegmentEnterTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<SegmentEnterTriggerJourneyNode> {
    override val nodeType: KClass<SegmentEnterTriggerJourneyNode> = SegmentEnterTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: SegmentEnterTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val segmentIdentity = payload["segmentIdentity"] as? String ?: error("Missing required payload param: segmentIdentity")

        val matched = node.segmentIdentity == null || segmentIdentity == node.segmentIdentity

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                "segmentIdentity" to segmentIdentity,
            ),
        )
    }
}

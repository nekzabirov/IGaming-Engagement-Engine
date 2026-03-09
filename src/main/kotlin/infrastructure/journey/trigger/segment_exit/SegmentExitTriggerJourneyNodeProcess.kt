package com.nekgambling.infrastructure.journey.trigger.segment_exit

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class SegmentExitTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<SegmentExitTriggerJourneyNode> {
    override val nodeType: KClass<SegmentExitTriggerJourneyNode> = SegmentExitTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: SegmentExitTriggerJourneyNode,
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

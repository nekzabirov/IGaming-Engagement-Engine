package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import kotlin.reflect.KClass

class ConditionJourneyNodeProcess : JourneyNodeProcess<IConditionJourneyNode> {
    override val nodeType: KClass<IConditionJourneyNode> = IConditionJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: IConditionJourneyNode,
        payload: Payload,
    ): JourneyNodeProcess.Response {
        val value = payload[node.inputKey] as? String
            ?: error("Missing required input key: ${node.inputKey}")

        val matched = node.evaluate(value)

        return JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }
}

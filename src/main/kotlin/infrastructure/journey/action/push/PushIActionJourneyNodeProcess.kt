package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.action.IActionJourneyNodeProcess
import kotlin.reflect.KClass

class PushIActionJourneyNodeProcess : IActionJourneyNodeProcess<IPushActionJourneyNode>() {
    override val nodeType: KClass<IPushActionJourneyNode> = IPushActionJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: IPushActionJourneyNode,
        payload: Payload
    ): JourneyNodeProcess.Response? {
        TODO("Not yet implemented")
    }
}
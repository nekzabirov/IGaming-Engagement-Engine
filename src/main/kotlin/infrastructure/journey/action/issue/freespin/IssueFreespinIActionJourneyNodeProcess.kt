package com.nekgambling.infrastructure.journey.action.issue.freespin

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.action.IActionJourneyNodeProcess
import kotlin.reflect.KClass

class IssueFreespinIActionJourneyNodeProcess : IActionJourneyNodeProcess<IssueFreespinActionJourneyNode>() {
    override val nodeType: KClass<IssueFreespinActionJourneyNode> = IssueFreespinActionJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: IssueFreespinActionJourneyNode,
        payload: Payload
    ): JourneyNodeProcess.Response? {
        // TODO: Implement freespin issuance logic
        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = emptyMap(),
        )
    }
}

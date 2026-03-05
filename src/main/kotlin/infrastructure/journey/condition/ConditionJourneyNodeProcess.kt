package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.condition.repository.IConditionRepository
import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import kotlin.reflect.KClass

class ConditionJourneyNodeProcess(
    private val conditionRepository: IConditionRepository,
) : JourneyNodeProcess<ConditionJourneyNode> {
    override val nodeType: KClass<ConditionJourneyNode> = ConditionJourneyNode::class

    override suspend fun process(playerId: String, node: ConditionJourneyNode, payload: Map<String, Any>): JourneyNodeProcess.Response? {
        val result = conditionRepository
            .findResultBy(playerId, node.condition.id)
            .orElse(null)
            ?: return null

        val next = if (result.passed) node.onMatch else node.onMismatch

        return JourneyNodeProcess.Response(nextNode = next, emptyMap())
    }
}

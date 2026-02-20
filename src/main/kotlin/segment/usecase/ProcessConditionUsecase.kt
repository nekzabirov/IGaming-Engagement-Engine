package com.nekgambling.segment.usecase

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.adapter.ILockAdapter
import com.nekgambling.segment.condition.ConditionRuleEvaluatorResolver
import com.nekgambling.segment.event.ConditionResultEvent
import com.nekgambling.segment.model.Condition
import com.nekgambling.segment.model.ConditionResult
import com.nekgambling.segment.repository.IConditionRepository

class ProcessConditionUsecase(
    private val evaluatorResolver: ConditionRuleEvaluatorResolver,
    private val conditionRepository: IConditionRepository,
    private val eventAdapter: IEventAdapter,
    private val lockAdapter: ILockAdapter
) {

    suspend operator fun invoke(playerId: String, condition: Condition): Result<ConditionResult> = runCatching {
        lockAdapter.withLock("condition:${condition.id}:player:$playerId") {
            process(playerId, condition)
        }
    }

    private suspend fun process(playerId: String, condition: Condition): ConditionResult {
        val isEvaluate = evaluatorResolver.evaluate(playerId, condition.rule)
        val result = ConditionResult(condition = condition, playerId = playerId, passed = isEvaluate)

        return conditionRepository.save(result).also {
            eventAdapter.publish(ConditionResultEvent(result = it))
        }
    }

}
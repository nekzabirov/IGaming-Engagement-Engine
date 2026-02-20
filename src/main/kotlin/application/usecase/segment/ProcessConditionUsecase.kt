package com.nekgambling.application.usecase.segment

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.domain.condition.strategy.ConditionRuleEvaluatorResolver
import com.nekgambling.application.event.segment.ConditionResultEvent
import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.condition.model.ConditionResult
import com.nekgambling.domain.condition.repository.IConditionRepository

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
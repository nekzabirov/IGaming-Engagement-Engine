package com.nekgambling.domain.condition.repository

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.condition.model.ConditionResult
import java.util.Optional

interface IConditionRepository {
    suspend fun save(condition: Condition): Condition

    suspend fun save(conditionResult: ConditionResult): ConditionResult

    suspend fun findResultBy(playerId: String, conditionId: Long): Optional<ConditionResult>
}
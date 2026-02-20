package com.nekgambling.segment.repository

import com.nekgambling.segment.model.Condition
import com.nekgambling.segment.model.ConditionResult
import java.util.Optional

interface IConditionRepository {
    suspend fun save(condition: Condition): Condition

    suspend fun save(conditionResult: ConditionResult): ConditionResult

    suspend fun findResultBy(playerId: String, conditionId: Long): Optional<ConditionResult>
}
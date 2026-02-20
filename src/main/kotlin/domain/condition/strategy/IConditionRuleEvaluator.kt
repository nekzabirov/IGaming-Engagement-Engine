package com.nekgambling.domain.condition.strategy

import com.nekgambling.domain.condition.model.IConditionRule
import kotlin.reflect.KClass

interface IConditionRuleEvaluator<T : IConditionRule> {
    val condition: KClass<T>

    suspend fun evaluate(playerId: String, condition: T): Boolean
}
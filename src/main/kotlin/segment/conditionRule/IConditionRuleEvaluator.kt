package com.nekgambling.segment.condition

import kotlin.reflect.KClass

interface IConditionRuleEvaluator<T : IConditionRule> {
    val condition: KClass<T>

    suspend fun evaluate(playerId: String, condition: T): Boolean
}
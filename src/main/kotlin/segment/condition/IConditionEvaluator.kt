package com.nekgambling.segment.condition

import kotlin.reflect.KClass

interface IConditionEvaluator<T : ICondition> {
    val condition: KClass<T>

    suspend fun evaluate(playerId: String, condition: T): Boolean
}
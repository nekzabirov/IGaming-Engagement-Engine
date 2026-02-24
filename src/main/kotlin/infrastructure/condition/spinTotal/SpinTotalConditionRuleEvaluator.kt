package com.nekgambling.infrastructure.condition.spinTotal

import com.nekgambling.application.query.QueryBus
import com.nekgambling.application.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlin.reflect.KClass

class SpinTotalConditionRuleEvaluator(private val queryBus: QueryBus) : IConditionRuleEvaluator<SpinTotalConditionRule> {
    override val condition: KClass<SpinTotalConditionRule> = SpinTotalConditionRule::class

    override suspend fun evaluate(playerId: String, condition: SpinTotalConditionRule): Boolean {
        val total = queryBus.execute(GetPlayerSpinTotalQuery(playerId, condition.date.toPeriod()))

        condition.placeAmount?.let {
            if (!it.check(total.placeAmount)) return false
        }

        condition.settleAmount?.let {
            if (!it.check(total.settleAmount)) return false
        }

        return true
    }
}

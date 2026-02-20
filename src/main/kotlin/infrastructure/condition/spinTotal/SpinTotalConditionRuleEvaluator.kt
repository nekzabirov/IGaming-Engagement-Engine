package com.nekgambling.infrastructure.condition.spinTotal

import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import com.nekgambling.infrastructure.clickhouse.reader.ClickHousePlayerSpinTotalReader
import kotlin.reflect.KClass

class SpinTotalConditionRuleEvaluator(private val playerSpinTotalReader: ClickHousePlayerSpinTotalReader) : IConditionRuleEvaluator<SpinTotalConditionRule> {
    override val condition: KClass<SpinTotalConditionRule> = SpinTotalConditionRule::class

    override suspend fun evaluate(playerId: String, condition: SpinTotalConditionRule): Boolean {
        val total = playerSpinTotalReader.read(playerId, condition.date.toPeriod())

        condition.placeAmount?.let {
            if (!it.check(total.placeAmount)) return false
        }

        condition.settleAmount?.let {
            if (!it.check(total.settleAmount)) return false
        }

        return true
    }
}
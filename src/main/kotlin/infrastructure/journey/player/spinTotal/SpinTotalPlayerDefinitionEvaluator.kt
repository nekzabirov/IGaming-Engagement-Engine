package com.nekgambling.infrastructure.journey.player.spinTotal

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlin.reflect.KClass

class SpinTotalPlayerDefinitionEvaluator(private val queryBus: QueryBus) : IPlayerDefinitionEvaluator<SpinTotalPlayerDefinition> {
    override val definition: KClass<SpinTotalPlayerDefinition> = SpinTotalPlayerDefinition::class

    override suspend fun evaluate(playerId: String, condition: SpinTotalPlayerDefinition): Boolean {
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

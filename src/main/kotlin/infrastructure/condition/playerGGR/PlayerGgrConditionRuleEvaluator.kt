package com.nekgambling.infrastructure.condition.playerGGR

import com.nekgambling.application.query.QueryBus
import com.nekgambling.application.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.application.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class PlayerGgrConditionRuleEvaluator(
    private val queryBus: QueryBus,
) : IConditionRuleEvaluator<PlayerGgrConditionRule> {
    override val condition: KClass<PlayerGgrConditionRule> = PlayerGgrConditionRule::class

    override suspend fun evaluate(playerId: String, condition: PlayerGgrConditionRule): Boolean = coroutineScope {
        val bonusPayOutputDeferred = async {
            queryBus.execute(GetPlayerBonusPayoutTotalQuery(playerId, condition.date.toPeriod()))
        }

        val totalSpinDeferred = async {
            queryBus.execute(GetPlayerSpinTotalQuery(playerId, condition.date.toPeriod()))
        }

        val playerGGR = totalSpinDeferred.await().placeAmount - totalSpinDeferred.await().settleAmount
        val playerNGR = totalSpinDeferred.await().realPlaceAmount - totalSpinDeferred.await().realPlaceAmount - bonusPayOutputDeferred.await()

        condition.ggr?.let { ggr ->
            if (!ggr.check(playerGGR)) return@coroutineScope false
        }

        condition.ngr?.let { ngr ->
            if (!ngr.check(playerNGR)) return@coroutineScope false
        }

        true
    }
}

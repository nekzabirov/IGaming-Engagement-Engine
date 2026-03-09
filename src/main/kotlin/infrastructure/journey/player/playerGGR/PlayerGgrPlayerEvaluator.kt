package com.nekgambling.infrastructure.journey.player.playerGGR

import com.nekgambling.application.query.QueryBus
import com.nekgambling.application.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.application.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class PlayerGgrPlayerEvaluator(
    private val queryBus: QueryBus,
) : IPlayerDefinitionEvaluator<PlayerGgrDefinition> {
    override val definition: KClass<PlayerGgrDefinition> = PlayerGgrDefinition::class

    override suspend fun evaluate(playerId: String, condition: PlayerGgrDefinition): Boolean = coroutineScope {
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

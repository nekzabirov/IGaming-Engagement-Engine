package com.nekgambling.infrastructure.condition.playerGGR

import com.nekgambling.application.reader.IPlayerBonusPayoutTotalReader
import com.nekgambling.application.reader.IPlayerSpinTotalReader
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class PlayerGgrConditionRuleEvaluator(
    private val playerSpinTotalReader: IPlayerSpinTotalReader,
    private val playerBonusPayoutTotalReader: IPlayerBonusPayoutTotalReader
) : IConditionRuleEvaluator<PlayerGgrConditionRule> {
    override val condition: KClass<PlayerGgrConditionRule> = PlayerGgrConditionRule::class

    override suspend fun evaluate(playerId: String, condition: PlayerGgrConditionRule): Boolean = coroutineScope {
        val bonusPayOutputDeferred = async {
            playerBonusPayoutTotalReader.read(playerId, condition.date.toPeriod())
        }

        val totalSpinDeferred = async {
            playerSpinTotalReader.read(playerId, condition.date.toPeriod())
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
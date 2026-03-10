package com.nekgambling.infrastructure.journey.player.playerGGR

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.application.cqrs.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.player.IPlayerJourneyNodeProcess
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class PlayerGgrPlayerJourneyNodeProcess(
    private val queryBus: QueryBus,
) : IPlayerJourneyNodeProcess<PlayerGgrPlayerJourneyNode> {

    override val nodeType: KClass<PlayerGgrPlayerJourneyNode> = PlayerGgrPlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: PlayerGgrPlayerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response = coroutineScope {
        val bonusPayOutputDeferred = async {
            queryBus.execute(GetPlayerBonusPayoutTotalQuery(playerId, node.date.toPeriod()))
        }

        val totalSpinDeferred = async {
            queryBus.execute(GetPlayerSpinTotalQuery(playerId, node.date.toPeriod()))
        }

        val totalSpin = totalSpinDeferred.await()
        val bonusPayOutput = bonusPayOutputDeferred.await()

        val playerGGR = totalSpin.placeAmount - totalSpin.settleAmount
        val playerNGR = totalSpin.realPlaceAmount - totalSpin.realPlaceAmount - bonusPayOutput

        val matched = (node.ggr == null || node.ggr.check(playerGGR))
                && (node.ngr == null || node.ngr.check(playerNGR))

        JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }
}

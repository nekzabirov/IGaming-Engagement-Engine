package com.nekgambling.infrastructure.journey.player.spinTotal

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.player.IPlayerJourneyNodeProcess
import kotlin.reflect.KClass

class SpinTotalPlayerJourneyNodeProcess(
    private val queryBus: QueryBus,
) : IPlayerJourneyNodeProcess<SpinTotalPlayerJourneyNode> {

    override val nodeType: KClass<SpinTotalPlayerJourneyNode> = SpinTotalPlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: SpinTotalPlayerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response {
        val total = queryBus.execute(GetPlayerSpinTotalQuery(playerId, node.date.toPeriod()))

        val matched = (node.placeAmount == null || node.placeAmount.check(total.placeAmount))
                && (node.settleAmount == null || node.settleAmount.check(total.settleAmount))

        return JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }
}

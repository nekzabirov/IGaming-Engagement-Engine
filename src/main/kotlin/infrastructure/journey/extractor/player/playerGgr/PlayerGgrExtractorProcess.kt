package com.nekgambling.infrastructure.journey.extractor.player.playerGgr

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.application.cqrs.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode.Companion.buildOutput
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class PlayerGgrExtractorProcess(
    private val queryBus: QueryBus,
) : ExtractorJourneyNodeProcess<PlayerGgrExtractor>() {

    override val nodeType: KClass<PlayerGgrExtractor> = PlayerGgrExtractor::class

    override suspend fun extract(
        playerId: String,
        node: PlayerGgrExtractor,
        payload: Payload,
    ): Payload = coroutineScope {
        val bonusPayoutDeferred = async {
            queryBus.execute(GetPlayerBonusPayoutTotalQuery(playerId, node.date.toPeriod()))
        }

        val totalSpinDeferred = async {
            queryBus.execute(GetPlayerSpinTotalQuery(playerId, node.date.toPeriod()))
        }

        val totalSpin = totalSpinDeferred.await()
        val bonusPayout = bonusPayoutDeferred.await()

        val ggr = totalSpin.placeAmount - totalSpin.settleAmount
        val ngr = totalSpin.realPlaceAmount - totalSpin.realSettleAmount - bonusPayout

        buildOutput(
            "ggr" to ggr,
            "ngr" to ngr,
        )
    }
}

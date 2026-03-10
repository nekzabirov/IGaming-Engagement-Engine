package com.nekgambling.infrastructure.journey.extractor.player.spinTotal

import com.nekgambling.application.cqrs.query.QueryBus
import com.nekgambling.application.cqrs.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode.Companion.buildOutput
import kotlin.reflect.KClass

class SpinTotalExtractorProcess(
    private val queryBus: QueryBus,
) : ExtractorJourneyNodeProcess<SpinTotalExtractor>() {

    override val nodeType: KClass<SpinTotalExtractor> = SpinTotalExtractor::class

    override suspend fun extract(
        playerId: String,
        node: SpinTotalExtractor,
        payload: Map<String, Any>,
    ): Map<String, Any> {
        val total = queryBus.execute(GetPlayerSpinTotalQuery(playerId, node.date.toPeriod()))

        return buildOutput(
            "placeAmount" to total.placeAmount,
            "settleAmount" to total.settleAmount,
            "realPlaceAmount" to total.realPlaceAmount,
            "realSettleAmount" to total.realSettleAmount,
        )
    }
}

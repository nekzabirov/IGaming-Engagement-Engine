package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class FreespinTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<FreespinTriggerJourneyNode> {
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: FreespinTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val freespinId = payload["freespinId"] as? String ?: error("Missing required payload param: freespinId")
        val freespinIdentity = payload["freespinIdentity"] as? String ?: error("Missing required payload param: freespinIdentity")
        val gameId = payload["gameId"] as? String ?: error("Missing required payload param: gameId")
        val freespinCurrency = payload["freespinCurrency"] as? String ?: error("Missing required payload param: freespinCurrency")
        val statusStr = payload["freespinStatus"] as? String ?: error("Missing required payload param: freespinStatus")
        val status = runCatching { PlayerFreespin.Status.valueOf(statusStr) }.getOrElse { error("Invalid freespin status: $statusStr") }
        val payoutRealAmount = (payload["freespinPayoutRealAmount"] as? Number)?.toLong()

        val matched = (node.freespinId == null || freespinId == node.freespinId)
                && (node.freespinIdentity == null || freespinIdentity == node.freespinIdentity)
                && (node.gameId == null || gameId == node.gameId)
                && (node.freespinStatus == null || status == node.freespinStatus)
                && (node.freespinPayoutRealAmount == null || payoutRealAmount != null && node.freespinPayoutRealAmount.check(payoutRealAmount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = buildMap {
                put("freespinId", freespinId)
                put("freespinIdentity", freespinIdentity)
                put("gameId", gameId)
                put("freespinCurrency", freespinCurrency)
                put("freespinStatus", statusStr)
                payoutRealAmount?.let { put("freespinPayoutRealAmount", it.toString()) }
            },
        )
    }
}

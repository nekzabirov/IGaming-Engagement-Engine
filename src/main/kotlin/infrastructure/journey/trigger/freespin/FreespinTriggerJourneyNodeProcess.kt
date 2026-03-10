package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class FreespinTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<FreespinTriggerJourneyNode> {
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: FreespinTriggerJourneyNode,
        payload: Payload,
    ): JourneyNodeProcess.Response? {
        val triggerName = payload["triggerName"] as? String ?: return null
        if (triggerName != FreespinTriggerJourneyNode.TRIGGER_NAME) return null

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
                put("freespin:id", freespinId)
                put("freespin:identity", freespinIdentity)
                put("freespin:gameId", gameId)
                put("freespin:currency", freespinCurrency)
                put("freespin:status", statusStr)
                payoutRealAmount?.let { put("freespin:payoutRealAmount", it.toString()) }
            },
        )
    }
}

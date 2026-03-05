package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class FreespinTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<FreespinTriggerJourneyNode> {
    override val node: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: FreespinTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val id = payload["id"] as? String ?: error("Missing required payload param: id")
        val identity = payload["identity"] as? String ?: error("Missing required payload param: identity")
        val game = payload["game"] as? String ?: error("Missing required payload param: game")
        val currency = payload["currency"] as? String ?: error("Missing required payload param: currency")
        val statusStr = payload["status"] as? String ?: error("Missing required payload param: status")
        val status = runCatching { PlayerFreespin.Status.valueOf(statusStr) }.getOrElse { error("Invalid freespin status: $statusStr") }
        val payoutRealAmount = (payload["payoutRealAmount"] as? Number)?.toLong()

        val matched = (node.id == null || id == node.id)
                && (node.identity == null || identity == node.identity)
                && (node.game == null || game == node.game)
                && (node.status == null || status == node.status)
                && (node.payoutRealAmount == null || payoutRealAmount != null && node.payoutRealAmount.check(payoutRealAmount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = buildMap {
                put("id", id)
                put("identity", identity)
                put("game", game)
                put("currency", currency)
                put("status", statusStr)
                payoutRealAmount?.let { put("payoutRealAmount", it.toString()) }
            },
        )
    }
}

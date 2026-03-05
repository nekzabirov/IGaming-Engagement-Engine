package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class BonusTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<BonusTriggerJourneyNode> {
    override val node: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: BonusTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val id = payload["id"] as? String ?: error("Missing required payload param: id")
        val identity = payload["identity"] as? String ?: error("Missing required payload param: identity")
        val statusStr = payload["status"] as? String ?: error("Missing required payload param: status")
        val status = runCatching { PlayerBonus.Status.valueOf(statusStr) }.getOrElse { error("Invalid bonus status: $statusStr") }
        val payoutAmount = (payload["payoutAmount"] as? Number)?.toLong() ?: error("Missing required payload param: payoutAmount")

        val matched = (node.id == null || id == node.id)
                && (node.identity == null || identity == node.identity)
                && (node.status == null || status == node.status)
                && (node.payoutAmount == null || node.payoutAmount.check(payoutAmount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                "id" to id,
                "identity" to identity,
                "status" to statusStr,
                "payoutAmount" to payoutAmount.toString(),
            ),
        )
    }
}

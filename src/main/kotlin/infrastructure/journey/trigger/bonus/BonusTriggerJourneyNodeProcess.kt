package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNodeProcess
import kotlin.reflect.KClass

class BonusTriggerJourneyNodeProcess : ITriggerJourneyNodeProcess<BonusTriggerJourneyNode> {
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: BonusTriggerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response? {
        val bonusId = payload["bonusId"] as? String ?: error("Missing required payload param: bonusId")
        val bonusIdentity = payload["bonusIdentity"] as? String ?: error("Missing required payload param: bonusIdentity")
        val statusStr = payload["bonusStatus"] as? String ?: error("Missing required payload param: bonusStatus")
        val status = runCatching { PlayerBonus.Status.valueOf(statusStr) }.getOrElse { error("Invalid bonus status: $statusStr") }
        val payoutAmount = (payload["bonusPayoutAmount"] as? Number)?.toLong() ?: error("Missing required payload param: bonusPayoutAmount")

        val matched = (node.bonusId == null || bonusId == node.bonusId)
                && (node.bonusIdentity == null || bonusIdentity == node.bonusIdentity)
                && (node.bonusStatus == null || status == node.bonusStatus)
                && (node.bonusPayoutAmount == null || node.bonusPayoutAmount.check(payoutAmount))

        if (!matched) return null

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = mapOf(
                "bonusId" to bonusId,
                "bonusIdentity" to bonusIdentity,
                "bonusStatus" to statusStr,
                "bonusPayoutAmount" to payoutAmount.toString(),
            ),
        )
    }
}

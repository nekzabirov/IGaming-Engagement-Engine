package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.strategy.JourneyNodeParams
import kotlin.reflect.KClass

object BonusTriggerJourneyNodeParams : JourneyNodeParams<BonusTriggerJourneyNode> {
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override fun inputParams(): Set<String> =
        setOf("bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")

    override fun outputParams(): Set<String> =
        setOf("bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")
}

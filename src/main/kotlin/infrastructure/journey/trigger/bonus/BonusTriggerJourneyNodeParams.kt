package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object BonusTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<BonusTriggerJourneyNode> {
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override fun inputParams(): Set<String> =
        setOf("bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")

    override fun outputParams(): Set<String> =
        setOf("bonus:id", "bonus:identity", "bonus:status", "bonus:payoutAmount")
}

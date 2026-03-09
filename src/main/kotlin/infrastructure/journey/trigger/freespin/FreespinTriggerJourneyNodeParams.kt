package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object FreespinTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<FreespinTriggerJourneyNode> {
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override fun inputParams(): Set<String> =
        setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus")

    override fun outputParams(): Set<String> =
        setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus", "freespinPayoutRealAmount")
}

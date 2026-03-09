package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.strategy.JourneyNodeParams
import kotlin.reflect.KClass

object PlayerJourneyNodeParams : JourneyNodeParams<PlayerJourneyNode> {
    override val nodeType: KClass<PlayerJourneyNode> = PlayerJourneyNode::class

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> =
        emptySet()
}

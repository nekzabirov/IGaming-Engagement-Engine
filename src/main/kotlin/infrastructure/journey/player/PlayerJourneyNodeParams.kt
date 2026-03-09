package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object PlayerJourneyNodeNomenclature : JourneyNodeNomenclature<PlayerJourneyNode> {
    override val nodeType: KClass<PlayerJourneyNode> = PlayerJourneyNode::class

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> =
        emptySet()
}

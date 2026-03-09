package com.nekgambling.infrastructure.journey.action

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object ActionJourneyNodeNomenclature : JourneyNodeNomenclature<IActionJourneyNode> {
    override val nodeType: KClass<IActionJourneyNode> = IActionJourneyNode::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}
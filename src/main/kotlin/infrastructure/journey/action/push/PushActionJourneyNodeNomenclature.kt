package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object PushActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IPushActionJourneyNode>() {
    override val nodeType: KClass<IPushActionJourneyNode> = IPushActionJourneyNode::class

    override fun inputParams(): Set<String> = setOf("locale")

    override fun outputParams(): Set<String> = emptySet()
}

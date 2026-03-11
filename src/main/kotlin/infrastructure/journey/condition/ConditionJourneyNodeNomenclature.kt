package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object ConditionJourneyNodeNomenclature : JourneyNodeNomenclature<IConditionJourneyNode> {
    override val nodeType: KClass<IConditionJourneyNode> = IConditionJourneyNode::class

    override val identity: String = "condition"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}

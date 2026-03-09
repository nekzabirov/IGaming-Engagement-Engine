package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object IssueFixedBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueFixedBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueFixedBonusActionJourneyNode> = IssueFixedBonusActionJourneyNode::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}

object IssueDynamicBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueDynamicBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueDynamicBonusActionJourneyNode> = IssueDynamicBonusActionJourneyNode::class

    override fun inputParams(): Set<String> = setOf("currency", "amount")

    override fun outputParams(): Set<String> = emptySet()
}

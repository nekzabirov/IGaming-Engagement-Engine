package com.nekgambling.infrastructure.journey.action.issue.freespin

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object IssueFreespinActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueFreespinActionJourneyNode>() {
    override val nodeType: KClass<IssueFreespinActionJourneyNode> = IssueFreespinActionJourneyNode::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}

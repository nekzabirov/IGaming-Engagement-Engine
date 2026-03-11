package com.nekgambling.infrastructure.journey.action.issue.freespin

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object IssueFreespinActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueFreespinActionJourneyNode>() {
    override val nodeType: KClass<IssueFreespinActionJourneyNode> = IssueFreespinActionJourneyNode::class

    override val identity: String = "issueFreespinAction"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun toAssetsMap(node: IssueFreespinActionJourneyNode): Map<String, Any> = mapOf(
        "freespinIdentity" to node.freespinIdentity,
    )

    override fun fromAssetsMap(map: Map<String, Any>): IssueFreespinActionJourneyNode = IssueFreespinActionJourneyNode(
        freespinIdentity = map["freespinIdentity"] as String,
    )
}

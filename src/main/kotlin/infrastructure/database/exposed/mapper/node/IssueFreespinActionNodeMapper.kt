package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNode
import kotlin.reflect.KClass

object IssueFreespinActionNodeMapper : JourneyNodeMapper<IssueFreespinActionJourneyNode> {
    override val type: String = "issueFreespinAction"
    override val nodeType: KClass<IssueFreespinActionJourneyNode> = IssueFreespinActionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): IssueFreespinActionJourneyNode = IssueFreespinActionJourneyNode(
        id = entity.id.value,
        next = next,
        identity = entity.nodeIdentity!!,
    )

    override fun applyToEntity(node: IssueFreespinActionJourneyNode, entity: JourneyNodeEntity) {
        entity.nodeIdentity = node.identity
    }
}

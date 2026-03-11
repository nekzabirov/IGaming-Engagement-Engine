package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNode
import kotlin.reflect.KClass

object IssueFreespinActionNodeMapper : IJourneyNodeMapper<IssueFreespinActionJourneyNode> {
    override val nodeType: KClass<IssueFreespinActionJourneyNode> = IssueFreespinActionJourneyNode::class
    override val identity: String = "issueFreespin"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): IssueFreespinActionJourneyNode {
        return IssueFreespinActionJourneyNode(
            id = entity.id.value,
            next = resolveNode(entity.next),
            freespinIdentity = entity.freespinIdentity!!,
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: IssueFreespinActionJourneyNode) {
        entity.nodeIdentity = node.freespinIdentity
        entity.freespinIdentity = node.freespinIdentity
    }
}

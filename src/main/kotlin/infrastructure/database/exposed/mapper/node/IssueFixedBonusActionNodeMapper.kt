package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueFixedBonusActionJourneyNode
import kotlin.reflect.KClass

object IssueFixedBonusActionNodeMapper : JourneyNodeMapper<IssueFixedBonusActionJourneyNode> {
    override val type: String = "issueFixedBonusAction"
    override val nodeType: KClass<IssueFixedBonusActionJourneyNode> = IssueFixedBonusActionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): IssueFixedBonusActionJourneyNode = IssueFixedBonusActionJourneyNode(
        id = entity.id.value,
        next = next,
        identity = entity.nodeIdentity!!,
        currency = Currency(entity.currency!!),
        amount = entity.amount!!,
    )

    override fun applyToEntity(node: IssueFixedBonusActionJourneyNode, entity: JourneyNodeEntity) {
        entity.nodeIdentity = node.identity
        entity.currency = node.currency.code
        entity.amount = node.amount
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueFixedBonusActionJourneyNode
import kotlin.reflect.KClass

object IssueFixedBonusActionNodeMapper : IJourneyNodeMapper<IssueFixedBonusActionJourneyNode> {
    override val nodeType: KClass<IssueFixedBonusActionJourneyNode> = IssueFixedBonusActionJourneyNode::class
    override val identity: String = "issueFixedBonus"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): IssueFixedBonusActionJourneyNode {
        return IssueFixedBonusActionJourneyNode(
            id = entity.id.value,
            next = resolveNode(entity.next),
            bonusIdentity = entity.bonusIdentity!!,
            currency = Currency(entity.currency!!),
            amount = entity.amount!!,
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: IssueFixedBonusActionJourneyNode) {
        entity.nodeIdentity = node.bonusIdentity
        entity.bonusIdentity = node.bonusIdentity
        entity.currency = node.currency.code
        entity.amount = node.amount
    }
}

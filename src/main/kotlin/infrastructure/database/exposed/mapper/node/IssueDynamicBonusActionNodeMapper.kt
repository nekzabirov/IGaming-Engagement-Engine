package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueDynamicBonusActionJourneyNode
import kotlin.reflect.KClass

object IssueDynamicBonusActionNodeMapper : IJourneyNodeMapper<IssueDynamicBonusActionJourneyNode> {
    override val nodeType: KClass<IssueDynamicBonusActionJourneyNode> = IssueDynamicBonusActionJourneyNode::class
    override val identity: String = "issueDynamicBonus"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): IssueDynamicBonusActionJourneyNode {
        return IssueDynamicBonusActionJourneyNode(
            id = entity.id.value,
            next = resolveNode(entity.next),
            bonusIdentity = entity.bonusIdentity!!,
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: IssueDynamicBonusActionJourneyNode) {
        entity.nodeIdentity = node.bonusIdentity
        entity.bonusIdentity = node.bonusIdentity
    }
}

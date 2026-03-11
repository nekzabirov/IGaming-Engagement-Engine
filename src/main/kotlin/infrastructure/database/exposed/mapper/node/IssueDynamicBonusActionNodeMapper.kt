package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueDynamicBonusActionJourneyNode
import kotlin.reflect.KClass

object IssueDynamicBonusActionNodeMapper : JourneyNodeMapper<IssueDynamicBonusActionJourneyNode> {
    override val type: String = "issueDynamicBonusAction"
    override val nodeType: KClass<IssueDynamicBonusActionJourneyNode> = IssueDynamicBonusActionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): IssueDynamicBonusActionJourneyNode = IssueDynamicBonusActionJourneyNode(
        id = entity.id.value,
        next = next,
        identity = entity.nodeIdentity!!,
    )

    override fun applyToEntity(node: IssueDynamicBonusActionJourneyNode, entity: JourneyNodeEntity) {
        entity.nodeIdentity = node.identity
    }
}

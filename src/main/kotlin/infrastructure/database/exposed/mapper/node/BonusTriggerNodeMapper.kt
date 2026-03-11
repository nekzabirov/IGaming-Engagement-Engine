package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object BonusTriggerNodeMapper : IJourneyNodeMapper<BonusTriggerJourneyNode> {
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class
    override val identity: String = "bonusTrigger"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): BonusTriggerJourneyNode {
        return BonusTriggerJourneyNode(
            id = entity.id.value,
            bonusId = entity.bonusId,
            bonusIdentity = entity.bonusIdentity,
            bonusStatus = entity.bonusStatus?.let { enumValueOf<PlayerBonus.Status>(it) },
            bonusPayoutAmount = entity.bonusPayoutAmount?.let { Json.decodeFromString<NumberParamValue>(it) },
            next = resolveNode(entity.next),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: BonusTriggerJourneyNode) {
        entity.bonusId = node.bonusId
        entity.bonusIdentity = node.bonusIdentity
        entity.bonusStatus = node.bonusStatus?.name
        entity.bonusPayoutAmount = node.bonusPayoutAmount?.let { Json.encodeToString(NumberParamValue.serializer(), it) }
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object BonusTriggerNodeMapper : JourneyNodeMapper<BonusTriggerJourneyNode> {
    override val type: String = "bonusTrigger"
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): BonusTriggerJourneyNode = BonusTriggerJourneyNode(
        id = entity.id.value,
        bonusId = entity.bonusId,
        bonusIdentity = entity.bonusIdentity,
        bonusStatus = entity.bonusStatus?.let { PlayerBonus.Status.valueOf(it) },
        bonusPayoutAmount = entity.bonusPayoutAmount?.let {
            Json.decodeFromString(NumberParamValue.serializer(), it)
        },
        next = next,
    )

    override fun applyToEntity(node: BonusTriggerJourneyNode, entity: JourneyNodeEntity) {
        entity.bonusId = node.bonusId
        entity.bonusIdentity = node.bonusIdentity
        entity.bonusStatus = node.bonusStatus?.name
        entity.bonusPayoutAmount = node.bonusPayoutAmount?.let {
            Json.encodeToString(NumberParamValue.serializer(), it)
        }
    }
}

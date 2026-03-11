package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object FreespinTriggerNodeMapper : JourneyNodeMapper<FreespinTriggerJourneyNode> {
    override val type: String = "freespinTrigger"
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): FreespinTriggerJourneyNode = FreespinTriggerJourneyNode(
        id = entity.id.value,
        freespinId = entity.freespinId,
        freespinIdentity = entity.freespinIdentity,
        gameId = entity.gameId,
        freespinStatus = entity.freespinStatus?.let { PlayerFreespin.Status.valueOf(it) },
        freespinPayoutRealAmount = entity.freespinPayoutRealAmount?.let {
            Json.decodeFromString(NumberParamValue.serializer(), it)
        },
        next = next,
    )

    override fun applyToEntity(node: FreespinTriggerJourneyNode, entity: JourneyNodeEntity) {
        entity.freespinId = node.freespinId
        entity.freespinIdentity = node.freespinIdentity
        entity.gameId = node.gameId
        entity.freespinStatus = node.freespinStatus?.name
        entity.freespinPayoutRealAmount = node.freespinPayoutRealAmount?.let {
            Json.encodeToString(NumberParamValue.serializer(), it)
        }
    }
}

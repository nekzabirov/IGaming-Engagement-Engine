package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

object FreespinTriggerNodeMapper : IJourneyNodeMapper<FreespinTriggerJourneyNode> {
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class
    override val identity: String = "freespinTrigger"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): FreespinTriggerJourneyNode {
        return FreespinTriggerJourneyNode(
            id = entity.id.value,
            freespinId = entity.freespinId,
            freespinIdentity = entity.freespinIdentity,
            gameId = entity.gameId,
            freespinStatus = entity.freespinStatus?.let { enumValueOf<PlayerFreespin.Status>(it) },
            freespinPayoutRealAmount = entity.freespinPayoutRealAmount?.let { Json.decodeFromString<NumberParamValue>(it) },
            next = resolveNode(entity.next),
        )
    }
}

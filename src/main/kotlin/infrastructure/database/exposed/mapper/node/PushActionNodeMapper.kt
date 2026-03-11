package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.entity.deserializePayload
import com.nekgambling.infrastructure.database.exposed.entity.serializePayload
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.push.EMailPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.IPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.InAppPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.SmsPushActionJourneyNode
import kotlin.reflect.KClass

object PushActionNodeMapper : JourneyNodeMapper<IPushActionJourneyNode> {
    override val type: String = "pushAction"
    override val nodeType: KClass<IPushActionJourneyNode> = IPushActionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): IPushActionJourneyNode {
        val templateId = entity.templateId!!
        val placeHolders = entity.placeHolders?.let { deserializePayload(it) } ?: emptyMap()
        val id = entity.id.value

        return when (entity.subType) {
            "email" -> EMailPushActionJourneyNode(id = id, next = next, templateId = templateId, placeHolders = placeHolders)
            "sms" -> SmsPushActionJourneyNode(id = id, next = next, templateId = templateId, placeHolders = placeHolders)
            "inApp" -> InAppPushActionJourneyNode(id = id, next = next, templateId = templateId, placeHolders = placeHolders)
            else -> error("Unknown push channel: ${entity.subType}")
        }
    }

    override fun applyToEntity(node: IPushActionJourneyNode, entity: JourneyNodeEntity) {
        entity.subType = when (node) {
            is EMailPushActionJourneyNode -> "email"
            is SmsPushActionJourneyNode -> "sms"
            is InAppPushActionJourneyNode -> "inApp"
        }
        entity.templateId = node.templateId
        entity.placeHolders = if (node.placeHolders.isNotEmpty()) {
            serializePayload(node.placeHolders)
        } else null
    }
}

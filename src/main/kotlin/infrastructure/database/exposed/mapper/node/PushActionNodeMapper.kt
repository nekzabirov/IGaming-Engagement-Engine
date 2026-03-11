package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.entity.deserializePayload
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.push.EMailPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.IPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.InAppPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.SmsPushActionJourneyNode
import kotlin.reflect.KClass

object PushActionNodeMapper : IJourneyNodeMapper<IPushActionJourneyNode> {
    override val nodeType: KClass<IPushActionJourneyNode> = IPushActionJourneyNode::class
    override val identity: String = "pushAction"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): IPushActionJourneyNode {
        val id = entity.id.value
        val next = resolveNode(entity.next)
        val templateId = entity.templateId!!
        val placeHolders = entity.placeHolders?.let { deserializePayload(it) } ?: emptyMap()

        return when (entity.subType) {
            "email" -> EMailPushActionJourneyNode(
                id = id,
                next = next,
                templateId = templateId,
                placeHolders = placeHolders,
            )
            "sms" -> SmsPushActionJourneyNode(
                id = id,
                next = next,
                templateId = templateId,
                placeHolders = placeHolders,
            )
            "inApp" -> InAppPushActionJourneyNode(
                id = id,
                next = next,
                templateId = templateId,
                placeHolders = placeHolders,
            )
            else -> error("Unknown push action subType: ${entity.subType}")
        }
    }
}

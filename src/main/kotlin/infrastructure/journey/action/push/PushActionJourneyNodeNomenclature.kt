package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object PushActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IPushActionJourneyNode>() {
    override val nodeType: KClass<IPushActionJourneyNode> = IPushActionJourneyNode::class

    override val identity: String = "pushAction"

    override fun inputParams(): Set<String> = setOf("locale")

    override fun outputParams(): Set<String> = emptySet()

    @Suppress("UNCHECKED_CAST")
    override fun toAssetsMap(node: IPushActionJourneyNode): Map<String, Any> = buildMap {
        put("channel", when (node) {
            is EMailPushActionJourneyNode -> "email"
            is SmsPushActionJourneyNode -> "sms"
            is InAppPushActionJourneyNode -> "inApp"
        })
        put("templateId", node.templateId)
        if (node.placeHolders.isNotEmpty()) put("placeHolders", node.placeHolders)
    }

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): IPushActionJourneyNode {
        val templateId = map["templateId"] as String
        val placeHolders = (map["placeHolders"] as? Map<String, Any>) ?: emptyMap()
        return when (map["channel"]) {
            "email" -> EMailPushActionJourneyNode(templateId = templateId, placeHolders = placeHolders)
            "sms" -> SmsPushActionJourneyNode(templateId = templateId, placeHolders = placeHolders)
            "inApp" -> InAppPushActionJourneyNode(templateId = templateId, placeHolders = placeHolders)
            else -> error("Unknown push channel: ${map["channel"]}")
        }
    }
}

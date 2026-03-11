package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNode
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

object PlacePayloadActionNodeMapper : IJourneyNodeMapper<PlacePayloadActionJourneyNode> {
    override val nodeType: KClass<PlacePayloadActionJourneyNode> = PlacePayloadActionJourneyNode::class
    override val identity: String = "placePayload"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): PlacePayloadActionJourneyNode {
        return PlacePayloadActionJourneyNode(
            id = entity.id.value,
            next = resolveNode(entity.next),
            key = entity.payloadKey!!,
            value = deserializeValue(entity.payloadValue!!),
        )
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: PlacePayloadActionJourneyNode) {
        entity.payloadKey = node.key
        entity.payloadValue = serializeValue(node.value)
    }

    private fun deserializeValue(json: String): Any {
        val element = Json.parseToJsonElement(json)
        if (element is JsonPrimitive) {
            return when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.longOrNull != null -> element.long
                element.doubleOrNull != null -> element.double
                else -> element.content
            }
        }
        return element.toString()
    }

    private fun serializeValue(value: Any): String = when (value) {
        is String -> Json.encodeToString(JsonPrimitive.serializer(), JsonPrimitive(value))
        is Number -> Json.encodeToString(JsonPrimitive.serializer(), JsonPrimitive(value))
        is Boolean -> Json.encodeToString(JsonPrimitive.serializer(), JsonPrimitive(value))
        else -> Json.encodeToString(JsonPrimitive.serializer(), JsonPrimitive(value.toString()))
    }
}

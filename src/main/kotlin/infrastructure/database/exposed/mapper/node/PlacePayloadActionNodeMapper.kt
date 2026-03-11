package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNode
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

object PlacePayloadActionNodeMapper : JourneyNodeMapper<PlacePayloadActionJourneyNode> {
    override val type: String = "placePayloadAction"
    override val nodeType: KClass<PlacePayloadActionJourneyNode> = PlacePayloadActionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): PlacePayloadActionJourneyNode = PlacePayloadActionJourneyNode(
        id = entity.id.value,
        next = next,
        key = entity.payloadKey!!,
        value = deserializeValue(entity.payloadValue!!),
    )

    override fun applyToEntity(node: PlacePayloadActionJourneyNode, entity: JourneyNodeEntity) {
        entity.payloadKey = node.key
        entity.payloadValue = serializeValue(node.value)
    }

    private fun serializeValue(value: Any): String =
        Json.encodeToString(JsonElement.serializer(), value.toJsonElement())

    private fun deserializeValue(json: String): Any {
        val element = Json.decodeFromString(JsonElement.serializer(), json)
        return element.toAny()
    }

    private fun Any.toJsonElement(): JsonElement = when (this) {
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        else -> JsonPrimitive(this.toString())
    }

    private fun JsonElement.toAny(): Any = when (this) {
        is JsonPrimitive -> when {
            isString -> content
            booleanOrNull != null -> boolean
            longOrNull != null -> long
            doubleOrNull != null -> double
            else -> content
        }
        else -> toString()
    }
}

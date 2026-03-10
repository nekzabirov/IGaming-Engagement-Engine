package com.nekgambling.infrastructure.database.exposed.entity

import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.database.exposed.table.JourneyInstantsTable
import kotlinx.serialization.json.*
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JourneyInstantEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<JourneyInstantEntity>(JourneyInstantsTable)

    var playerId by JourneyInstantsTable.playerId
    var journey by JourneyEntity referencedOn JourneyInstantsTable.journey
    var currentNode by JourneyNodeEntity referencedOn JourneyInstantsTable.currentNode
    var payload by JourneyInstantsTable.payload
}

internal fun serializePayload(payload: Payload): String {
    val jsonObject = buildJsonObject {
        payload.forEach { (key, value) ->
            when (value) {
                is String -> put(key, value)
                is Number -> put(key, value.toDouble())
                is Boolean -> put(key, value)
                else -> put(key, value.toString())
            }
        }
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}

internal fun deserializePayload(json: String): Payload {
    val jsonObject = Json.decodeFromString<JsonObject>(json)
    return jsonObject.mapValues { (_, element) ->
        when (element) {
            is JsonPrimitive -> when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.boolean
                element.longOrNull != null -> element.long
                element.doubleOrNull != null -> element.double
                else -> element.content
            }
            else -> element.toString()
        }
    }
}

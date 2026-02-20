package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.condition.model.ConditionResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun ConditionResult.toJson(): JsonObject = buildJsonObject {
    put("conditionId", condition.id)
    put("playerId", playerId)
    put("passed", passed)
    put("updatedAt", updatedAt.toString())
}

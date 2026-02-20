package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.segment.model.SegmentResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun SegmentResult.toJson(): JsonObject = buildJsonObject {
    put("playerId", playerId)
    put("segmentId", segment.id)
    put("segmentIdentity", segment.identity)
    put("passed", passed)
    put("updatedAt", updatedAt.toString())
}

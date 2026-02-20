package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.player.model.PlayerBonus
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun PlayerBonus.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("identity", identity)
    put("playerId", playerId)
    put("status", status.name)
    put("amount", amount)
    put("payoutAmount", payoutAmount)
}

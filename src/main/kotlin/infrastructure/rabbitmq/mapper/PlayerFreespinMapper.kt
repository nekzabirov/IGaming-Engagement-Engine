package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.player.model.PlayerFreespin
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun PlayerFreespin.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("identity", identity)
    put("playerId", playerId)
    put("game", game)
    put("status", status.name)
    put("payoutRealAmount", payoutRealAmount)
}

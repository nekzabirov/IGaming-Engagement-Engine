package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.player.model.PlayerSpin
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun PlayerSpin.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("playerId", playerId)
    freespinId?.let { put("freespinId", it) }
    put("spinCurrency", spinCurrency.code)
    put("game", game)
    put("placeRealAmount", placeRealAmount)
    put("settleRealAmount", settleRealAmount)
    put("placeBonusAmount", placeBonusAmount)
    put("settleBonusAmount", settleBonusAmount)
    put("createdAt", createdAt.toString())
}

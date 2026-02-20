package com.nekgambling.infrastructure.rabbitmq.mapper

import com.nekgambling.domain.player.model.PlayerInvoice
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun PlayerInvoice.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("playerId", playerId)
    put("type", type.name)
    put("status", status.name)
    put("transactionCurrency", transactionCurrency.code)
    put("amount", amount)
    put("transactionAmount", transactionAmount)
    put("taxAmount", taxAmount)
    put("feeAmount", feeAmount)
    put("createdAt", createdAt.toString())
}

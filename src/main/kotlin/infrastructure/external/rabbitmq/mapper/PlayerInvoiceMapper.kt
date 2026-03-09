package com.nekgambling.infrastructure.external.rabbitmq.mapper

import com.nekgambling.domain.model.player.PlayerInvoice
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

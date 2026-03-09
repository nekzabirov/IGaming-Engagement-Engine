package com.nekgambling.infrastructure.external.rabbitmq.mapper

import com.nekgambling.domain.model.player.PlayerDetails
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun PlayerDetails.toJson(playerId: String): JsonObject = buildJsonObject {
    put("playerId", playerId)
    put("id", id)
    username?.let { put("username", it) }
    email?.let { put("email", it) }
    phone?.let { put("phone", it) }
    put("emailConfirmed", emailConfirmed)
    put("phoneConfirmed", phoneConfirmed)
    put("status", status.name)
    firstName?.let { put("firstName", it) }
    lastName?.let { put("lastName", it) }
    middleName?.let { put("middleName", it) }
    birthDate?.let { put("birthDate", it.toString()) }
    country?.let { put("country", it.code) }
    locale?.let { put("locale", it.value) }
    personalNumber?.let { put("personalNumber", it) }
    put("isVerified", isVerified)
    gender?.let { put("gender", it.name) }
    address?.let { put("address", it) }
    affiliateTag?.let { put("affiliateTag", it) }
    put("registeredAt", registeredAt.toString())
}

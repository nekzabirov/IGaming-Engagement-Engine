package com.nekgambling.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class JourneyResponse(
    val id: Long,
    val identity: String,
    val head: JourneyNodeResponse,
)

@Serializable
data class JourneyNodeResponse(
    val id: Long,
    val type: String,
    val assets: Map<String, JsonElement> = emptyMap(),
    val next: JourneyNodeResponse? = null,
    val matchNode: JourneyNodeResponse? = null,
    val notMatchNode: JourneyNodeResponse? = null,
)

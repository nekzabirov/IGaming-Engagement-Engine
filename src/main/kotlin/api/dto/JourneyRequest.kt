package com.nekgambling.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CreateJourneyRequest(
    val identity: String,
    val head: JourneyNodeRequest,
)

@Serializable
data class JourneyNodeRequest(
    val type: String,
    val assets: Map<String, JsonElement> = emptyMap(),
    val next: JourneyNodeRequest? = null,
    val matchNode: JourneyNodeRequest? = null,
    val notMatchNode: JourneyNodeRequest? = null,
)

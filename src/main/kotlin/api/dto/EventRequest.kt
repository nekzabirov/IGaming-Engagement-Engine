package com.nekgambling.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    @SerialName("event_type")
    val type: String,

    @SerialName("user_ext_id")
    val userExtId: String,

    val payload: EventPayload,
)


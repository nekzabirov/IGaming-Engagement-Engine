package com.nekgambling.domain.model.journey

data class JourneyInstant(
    val id: Long = Long.MIN_VALUE,
    val playerId: String,
    val journey: Journey,
    val currentNode: IJourneyNode,
    val payload: Map<String, Any>
)

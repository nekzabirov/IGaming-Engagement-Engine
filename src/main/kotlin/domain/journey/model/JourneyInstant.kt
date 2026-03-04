package com.nekgambling.domain.journey.model

data class JourneyInstant(
    val playerId: String,
    val journey: Journey,
    val currentNode: IJourneyNode,
    val payload: Map<String, Any>
)
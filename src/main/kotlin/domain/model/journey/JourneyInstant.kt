package com.nekgambling.domain.model.journey

import com.nekgambling.domain.vo.Payload

data class JourneyInstant(
    val id: Long = Long.MIN_VALUE,
    val playerId: String,
    val journey: Journey,
    val currentNode: IJourneyNode,
    val payload: Payload
)

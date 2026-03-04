package com.nekgambling.domain.journey.model

data class Journey(
    val id: Long = Long.MIN_VALUE,
    val identity: String,
    val head: IJourneyNode
)

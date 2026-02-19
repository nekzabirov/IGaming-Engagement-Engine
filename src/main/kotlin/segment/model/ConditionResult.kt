package com.nekgambling.segment.model

data class ConditionResult(
    val conditionId: Int,
    val playerId: String,
    val passed: Boolean,
)

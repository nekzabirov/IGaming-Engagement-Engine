package com.nekgambling.domain.condition.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ConditionResult(
    val condition: Condition,
    val playerId: String,
    val passed: Boolean,
    val updatedAt: Instant = Clock.System.now(),
)

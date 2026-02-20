package com.nekgambling.domain.segment.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class SegmentResult(
    val playerId: String,
    val segment: Segment,
    val passed: Boolean,
    val updatedAt: Instant = Clock.System.now(),
)

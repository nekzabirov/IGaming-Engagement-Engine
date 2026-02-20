package com.nekgambling.domain.segment.model

import com.nekgambling.domain.condition.model.Condition

data class Segment(
    val id: Int,

    val identity: String,

    val conditions: List<Condition>
)
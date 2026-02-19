package com.nekgambling.segment.model

data class Segment(
    val id: Int,

    val identity: String,

    val conditions: List<Condition>
)
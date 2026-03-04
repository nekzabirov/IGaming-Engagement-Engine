package com.nekgambling.domain.trigger.model

data class Trigger(
    val id: Long = Long.MIN_VALUE,
    val rule: ITriggerRule
)

package com.nekgambling.domain.condition.model

data class Condition(
    val id: Long = Long.MIN_VALUE,
    val rule: IConditionRule,
)

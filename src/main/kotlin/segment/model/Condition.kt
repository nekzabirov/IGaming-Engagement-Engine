package com.nekgambling.segment.model

import com.nekgambling.segment.condition.IConditionRule

data class Condition(
    val id: Long = Long.MIN_VALUE,
    val rule: IConditionRule,
)

package com.nekgambling.segment.model

import com.nekgambling.segment.condition.IConditionRule

data class Condition(
    val id: Int,
    val rule: IConditionRule,
)

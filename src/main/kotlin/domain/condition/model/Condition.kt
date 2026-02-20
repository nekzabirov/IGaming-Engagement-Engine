package com.nekgambling.domain.condition.model

import com.nekgambling.domain.condition.model.IConditionRule

data class Condition(
    val id: Long = Long.MIN_VALUE,
    val rule: IConditionRule,
)

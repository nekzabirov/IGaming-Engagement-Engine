package com.nekgambling.infrastructure.condition.playerAge

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.condition.util.NumberParamValue

data class PlayerAgeConditionRule(val value: NumberParamValue) : IConditionRule
package com.nekgambling.infrastructure.condition.playerAge

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.condition.util.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerAge")
data class PlayerAgeConditionRule(val value: NumberParamValue) : IConditionRule

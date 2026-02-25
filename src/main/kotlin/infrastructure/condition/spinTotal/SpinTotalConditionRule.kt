package com.nekgambling.infrastructure.condition.spinTotal

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.condition.util.DateParamValue
import com.nekgambling.domain.condition.util.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("spinTotal")
data class SpinTotalConditionRule(
    val placeAmount: NumberParamValue? = null,
    val settleAmount: NumberParamValue? = null,

    val date: DateParamValue,
) : IConditionRule

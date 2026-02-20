package com.nekgambling.infrastructure.condition.playerGGR

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.condition.util.DateParamValue
import com.nekgambling.domain.condition.util.NumberParamValue

data class PlayerGgrConditionRule(
    val ggr: NumberParamValue? = null,

    val ngr: NumberParamValue? = null,

    val date: DateParamValue,
) : IConditionRule

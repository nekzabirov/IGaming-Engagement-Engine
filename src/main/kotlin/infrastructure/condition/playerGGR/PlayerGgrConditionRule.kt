package com.nekgambling.infrastructure.condition.playerGGR

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerGgr")
data class PlayerGgrConditionRule(
    val ggr: NumberParamValue? = null,

    val ngr: NumberParamValue? = null,

    val date: DateParamValue,
) : IConditionRule

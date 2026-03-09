package com.nekgambling.infrastructure.journey.player.spinTotal

import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("spinTotal")
data class SpinTotalPlayerDefinition(
    val placeAmount: NumberParamValue? = null,
    val settleAmount: NumberParamValue? = null,

    val date: DateParamValue,
) : IPlayerDefinition

package com.nekgambling.infrastructure.journey.player.playerGGR

import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerGgr")
data class PlayerGgrDefinition(
    val ggr: NumberParamValue? = null,

    val ngr: NumberParamValue? = null,

    val date: DateParamValue,
) : IPlayerDefinition

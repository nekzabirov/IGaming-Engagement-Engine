package com.nekgambling.infrastructure.journey.player.playerAge

import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("playerAge")
data class PlayerAgeDefinition(val value: NumberParamValue) : IPlayerDefinition

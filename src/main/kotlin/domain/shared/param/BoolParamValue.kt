package com.nekgambling.domain.vo.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("bool")
data class BoolParamValue(val value: Boolean) : ParamValue()

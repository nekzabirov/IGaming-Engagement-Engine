package com.nekgambling.domain.condition.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("bool")
data class BoolParamValue(val value: Boolean) : ParamValue()

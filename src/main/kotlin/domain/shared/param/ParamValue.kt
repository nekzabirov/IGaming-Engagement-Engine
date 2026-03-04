package com.nekgambling.domain.vo.param

import kotlinx.serialization.Serializable

@Serializable
sealed class ParamValue

@Serializable
@kotlinx.serialization.SerialName("string")
data class StringParamValue(val value: String) : ParamValue()

package com.nekgambling.domain.condition.util

data class BoolParamValue(val value: Boolean) : ParamValue(if (value) "TRUE" else "FALSE")

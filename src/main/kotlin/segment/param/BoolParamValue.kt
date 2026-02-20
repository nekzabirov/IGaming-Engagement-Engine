package com.nekgambling.segment.param

data class BoolParamValue(val value: Boolean) : ParamValue(if (value) "TRUE" else "FALSE")

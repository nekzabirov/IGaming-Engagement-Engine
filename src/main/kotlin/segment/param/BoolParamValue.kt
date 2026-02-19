package com.nekgambling.segment.param

data class BoolParamValue(val value: Boolean) : IParamValue(if (value) "TRUE" else "FALSE")

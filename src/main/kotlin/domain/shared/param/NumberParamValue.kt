package com.nekgambling.domain.vo.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NumberParamValue : ParamValue() {
    abstract fun check(value: Number): Boolean
}

@Serializable
@SerialName("numberMore")
class NumberMoreParamValue(val value: Long) : NumberParamValue() {
    override fun check(value: Number): Boolean =
        value.toLong() > this.value
}

package com.nekgambling.segment.param

sealed class NumberParamValue : ParamValue(null) {
    abstract fun check(value: Number): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is Number) return false
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class NumberMoreParamValue(private val value: Number) : NumberParamValue() {
    //TODO: Maybe more better is use float
    override fun check(value: Number): Boolean =
        value.toLong() > this.value.toLong()
}
package com.nekgambling.segment.param

sealed class IParamValue(private val value: String?) {
    override fun equals(other: Any?): Boolean {
        return value == other.toString()
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

data class StringParamValue(val value: String) : IParamValue(value)
package com.nekgambling.domain.condition.util

class NullEmptyParamValue : ParamValue(null) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return true
        return other.toString().isBlank()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

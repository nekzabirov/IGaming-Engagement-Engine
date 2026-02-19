package com.nekgambling.segment.param

class NullEmptyParamValue : IParamValue(null) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return true
        return other.toString().isBlank()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

package com.nekgambling.domain.vo.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("nullEmpty")
class NullEmptyParamValue : ParamValue() {
    override fun equals(other: Any?): Boolean {
        if (other == null) return true
        return other.toString().isBlank()
    }

    override fun hashCode(): Int = 0
}

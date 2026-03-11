package com.nekgambling.domain.asset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface NumberParamValue {

    fun matches(value: Number): Boolean

    @Serializable
    @SerialName("greaterThan")
    data class GreaterThan(val threshold: Double) : NumberParamValue {
        override fun matches(value: Number): Boolean = value.toDouble() > threshold
    }

    @Serializable
    @SerialName("lessThan")
    data class LessThan(val threshold: Double) : NumberParamValue {
        override fun matches(value: Number): Boolean = value.toDouble() < threshold
    }

    @Serializable
    @SerialName("inRange")
    data class InRange(val min: Double, val max: Double) : NumberParamValue {
        override fun matches(value: Number): Boolean = value.toDouble() in min..max
    }

    @Serializable
    @SerialName("equalTo")
    data class EqualTo(val target: Double) : NumberParamValue {
        override fun matches(value: Number): Boolean = value.toDouble() == target
    }

    fun toMap(): Map<String, Any> = when (this) {
        is GreaterThan -> mapOf("type" to "greaterThan", "threshold" to threshold)
        is LessThan -> mapOf("type" to "lessThan", "threshold" to threshold)
        is InRange -> mapOf("type" to "inRange", "min" to min, "max" to max)
        is EqualTo -> mapOf("type" to "equalTo", "target" to target)
    }

    companion object {
        fun fromMap(map: Map<String, Any>): NumberParamValue = when (map["type"]) {
            "greaterThan" -> GreaterThan((map["threshold"] as Number).toDouble())
            "lessThan" -> LessThan((map["threshold"] as Number).toDouble())
            "inRange" -> InRange((map["min"] as Number).toDouble(), (map["max"] as Number).toDouble())
            "equalTo" -> EqualTo((map["target"] as Number).toDouble())
            else -> error("Unknown NumberParamValue type: ${map["type"]}")
        }
    }
}

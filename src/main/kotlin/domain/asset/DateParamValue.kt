package com.nekgambling.domain.asset

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DateParamValue {

    @Serializable
    @SerialName("lastDays")
    data class LastDays(val days: Int) : DateParamValue

    @Serializable
    @SerialName("range")
    data class Range(val from: Instant, val to: Instant) : DateParamValue

    fun toMap(): Map<String, Any> = when (this) {
        is LastDays -> mapOf("type" to "lastDays", "days" to days)
        is Range -> mapOf("type" to "range", "from" to from.toString(), "to" to to.toString())
    }

    companion object {
        fun fromMap(map: Map<String, Any>): DateParamValue = when (map["type"]) {
            "lastDays" -> LastDays((map["days"] as Number).toInt())
            "range" -> Range(Instant.parse(map["from"] as String), Instant.parse(map["to"] as String))
            else -> error("Unknown DateParamValue type: ${map["type"]}")
        }
    }
}

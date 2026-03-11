package com.nekgambling.domain.asset

import com.nekgambling.domain.vo.Period
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days as durationDays

@Serializable
sealed interface DateParamValue {

    fun toPeriod(): Period

    @Serializable
    @SerialName("lastDays")
    data class LastDays(val days: Int) : DateParamValue {
        override fun toPeriod(): Period {
            val now = Clock.System.now()
            val from = now.minus(days.durationDays)
            return from to now
        }
    }

    @Serializable
    @SerialName("range")
    data class Range(val from: Instant, val to: Instant) : DateParamValue {
        override fun toPeriod(): Period = from to to
    }

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

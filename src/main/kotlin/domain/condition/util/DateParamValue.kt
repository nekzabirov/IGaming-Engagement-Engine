package com.nekgambling.domain.condition.util

import com.nekgambling.domain.vo.Period
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
sealed class DateParamValue : ParamValue() {
    abstract fun toPeriod(): Period
}

@Serializable
@SerialName("dateBefore")
class DateBeforeParamValue(val date: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        Instant.DISTANT_PAST to date
}

@Serializable
@SerialName("dateAfter")
class DateAfterParamValue(val date: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        date to Instant.DISTANT_FUTURE
}

@Serializable
@SerialName("dateRange")
class DateRangeParamValue(val from: Instant, val until: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        from to until
}

@Serializable
@SerialName("dateLastDays")
class DateLastDays(val days: Int) : DateParamValue() {
    override fun toPeriod(): Period {
        val now = Clock.System.now()
        val from = now.minus(days.days)
        return from to now
    }
}

package com.nekgambling.domain.condition.util

import com.nekgambling.domain.vo.Period
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

sealed class DateParamValue : ParamValue(null) {
    protected abstract fun check(date: Instant): Boolean

    abstract fun toPeriod(): Period

    override fun equals(other: Any?): Boolean {
        if (other !is Instant) return false

        return check(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class DateBeforeParamValue(private val date: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        Instant.DISTANT_PAST to date

    override fun check(date: Instant): Boolean =
        this.date < date
}

class DateAfterParamValue(private val date: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        date to Instant.DISTANT_FUTURE

    override fun check(date: Instant): Boolean =
        this.date >= date
}

class DateRangeParamValue(private val from: Instant, private val until: Instant) : DateParamValue() {
    override fun toPeriod(): Period =
        from to until

    override fun check(date: Instant): Boolean =
        this.from <= date && until <= date
}

class DateLastDays(private val days: Int) : DateParamValue() {
    override fun toPeriod(): Period {
        val now = Clock.System.now()

        val from = now.minus(days.days)

        return from to now
    }

    override fun check(date: Instant): Boolean {
        val now = Clock.System.now()

        val distance = now - date

        return distance.inWholeDays <= days
    }
}

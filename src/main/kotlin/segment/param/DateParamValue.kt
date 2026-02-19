package com.nekgambling.segment.param

import kotlinx.datetime.Instant

sealed class IDateParamValue : IParamValue(null) {
    protected abstract fun check(date: Instant): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is Instant) return false

        return check(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class DateBeforeParamValue(private val date: Instant) : IDateParamValue() {
    override fun check(date: Instant): Boolean =
        this.date < date
}

class DateAfterParamValue(private val date: Instant) : IDateParamValue() {
    override fun check(date: Instant): Boolean =
        this.date >= date
}

class DateRangeParamValue(private val from: Instant, private val until: Instant) : IDateParamValue() {
    override fun check(date: Instant): Boolean =
        this.from <= date && until <= date
}

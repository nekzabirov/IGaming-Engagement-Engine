package com.nekgambling.infrastructure.external.currency

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.domain.vo.Currency

class UnitsCurrencyAdapter : ICurrencyAdapter {

    override suspend fun convertUnitsToSystemUnits(amount: Long, currency: Currency): Long = amount

    override suspend fun convertToUnits(
        amount: Double,
        currency: Currency
    ): Long = (amount * 100L).toLong()
}

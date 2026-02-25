package com.nekgambling.infrastructure.currency

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.domain.vo.Currency

class UnitsCurrencyAdapter : ICurrencyAdapter {

    override suspend fun convertUnitsToSystemUnits(amount: Long, currency: Currency): Long = amount
}

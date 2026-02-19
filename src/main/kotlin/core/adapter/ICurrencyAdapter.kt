package com.nekgambling.core.adapter

import com.nekgambling.core.vo.Currency

interface ICurrencyAdapter {

    suspend fun convertUnitsToSystemUnits(amount: Long, currency: Currency): Long

}
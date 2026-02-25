package com.nekgambling.application.adapter

import com.nekgambling.domain.vo.Currency

interface ICurrencyAdapter {

    suspend fun convertUnitsToSystemUnits(amount: Long, currency: Currency): Long

    suspend fun convertToUnits(amount: Double, currency: Currency): Long

}
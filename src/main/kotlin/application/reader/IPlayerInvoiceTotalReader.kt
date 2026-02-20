package com.nekgambling.application.reader

import com.nekgambling.domain.vo.Period

interface IPlayerInvoiceTotalReader {
    data class Result(
        val depositAmount: Long,
        val withdrawAmount: Long,

        val depositCount: Int,
        val withdrawCount: Int,

        val taxAmount: Long,
        val feesAmount: Long,
    )

    suspend fun read(playerId: String, period: Period): Result

}
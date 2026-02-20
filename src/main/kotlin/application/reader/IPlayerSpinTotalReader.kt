package com.nekgambling.application.reader

import com.nekgambling.domain.vo.Period

interface IPlayerSpinTotalReader {
    data class Result(
        val placeAmount: Long,
        val settleAmount: Long,

        val realPlaceAmount: Long,
        val realSettleAmount: Long,
    )

    suspend fun read(playerId: String, period: Period): Result

}
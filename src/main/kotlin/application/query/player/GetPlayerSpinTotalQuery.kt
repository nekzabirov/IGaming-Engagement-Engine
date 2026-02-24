package com.nekgambling.application.query.player

import com.nekgambling.application.query.IQuery
import com.nekgambling.domain.vo.Period

data class GetPlayerSpinTotalQuery(
    val playerId: String,
    val period: Period,
) : IQuery<GetPlayerSpinTotalQuery.Result> {
    data class Result(
        val placeAmount: Long,
        val settleAmount: Long,
        val realPlaceAmount: Long,
        val realSettleAmount: Long,
    )
}

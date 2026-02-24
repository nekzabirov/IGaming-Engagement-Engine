package com.nekgambling.application.query.player

import com.nekgambling.application.query.IQuery
import com.nekgambling.domain.vo.Period

data class GetPlayerInvoiceTotalQuery(
    val playerId: String,
    val period: Period,
) : IQuery<GetPlayerInvoiceTotalQuery.Result> {
    data class Result(
        val depositAmount: Long,
        val withdrawAmount: Long,
        val depositCount: Int,
        val withdrawCount: Int,
        val taxAmount: Long,
        val feesAmount: Long,
    )
}

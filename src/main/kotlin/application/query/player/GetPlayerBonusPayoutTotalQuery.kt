package com.nekgambling.application.query.player

import com.nekgambling.application.query.IQuery
import com.nekgambling.domain.vo.Period

data class GetPlayerBonusPayoutTotalQuery(
    val playerId: String,
    val period: Period,
) : IQuery<Long>

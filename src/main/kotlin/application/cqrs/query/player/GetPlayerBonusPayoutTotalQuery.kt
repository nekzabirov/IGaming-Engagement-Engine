package com.nekgambling.application.cqrs.query.player

import com.nekgambling.application.cqrs.query.IQuery
import com.nekgambling.domain.vo.Period

data class GetPlayerBonusPayoutTotalQuery(
    val playerId: String,
    val period: Period,
) : IQuery<Long>

package com.nekgambling.domain.player.model

import com.nekgambling.domain.vo.Currency
import kotlinx.datetime.Instant

data class PlayerSpin(
    val id: String,

    val playerId: String,

    val freespinId: String? = null,

    val spinCurrency: Currency,

    val game: String,

    val placeRealAmount: Long,
    val settleRealAmount: Long,

    val placeBonusAmount: Long,
    val settleBonusAmount: Long,

    val createdAt: Instant,
)

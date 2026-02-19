package com.nekgambling.player.model

import com.nekgambling.core.vo.Currency
import kotlin.time.Instant

data class PlayerSpin(
    val id: String,

    val playerId: String,

    val freespinId: String? = null,

    val spinCurrency: Currency,

    val game: String,

    val placeAmount: Long,
    val settleAmount: Long,

    val createdAt: Instant,
)

package com.nekgambling.domain.model.player

import kotlinx.serialization.Serializable

data class PlayerBonus(
    val id: String,

    val identity: String,

    val playerId: String,

    val status: Status,

    val amount: Long,

    val payoutAmount: Long,
) {
    @Serializable
    enum class Status {
        ISSUE,
        WAGERING,
        LOST,
        WAGERED
    }
}

package com.nekgambling.domain.model.player

data class PlayerBonus(
    val id: String,

    val identity: String,

    val playerId: String,

    val status: Status,

    val amount: Long,

    val payoutAmount: Long,
) {
    enum class Status {
        ISSUE,
        WAGERING,
        LOST,
        WAGERED
    }
}

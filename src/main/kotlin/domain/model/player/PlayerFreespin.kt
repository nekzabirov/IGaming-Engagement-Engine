package com.nekgambling.domain.model.player

data class PlayerFreespin(
    val id: String,

    val identity: String,

    val playerId: String,

    val game: String,

    val status: Status,

    val payoutRealAmount: Long,
) {
    enum class Status {
        ISSUE,
        ACTIVE,
        CANCELLED,
        PLAYED
    }
}

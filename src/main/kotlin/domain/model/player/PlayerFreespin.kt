package com.nekgambling.domain.model.player

import kotlinx.serialization.Serializable

data class PlayerFreespin(
    val id: String,

    val identity: String,

    val playerId: String,

    val game: String,

    val status: Status,

    val payoutRealAmount: Long,
) {
    @Serializable
    enum class Status {
        ISSUE,
        ACTIVE,
        CANCELLED,
        PLAYED
    }
}

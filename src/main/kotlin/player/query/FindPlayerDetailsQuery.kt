package com.nekgambling.player.query

import com.nekgambling.player.model.PlayerDetails

interface FindPlayerDetailsQuery {
    suspend fun execute(playerId: String): Result<PlayerDetails>
}
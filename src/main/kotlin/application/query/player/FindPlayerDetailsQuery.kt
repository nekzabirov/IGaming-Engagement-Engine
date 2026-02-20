package com.nekgambling.application.query.player

import com.nekgambling.domain.player.model.PlayerDetails

interface FindPlayerDetailsQuery {
    suspend fun execute(playerId: String): Result<PlayerDetails>
}
package com.nekgambling.player.repository

import com.nekgambling.player.model.PlayerDetails
import java.util.Optional

interface IPlayerDetailsRepository {
    suspend fun save(data: PlayerDetails): PlayerDetails

    suspend fun findById(id: String): Optional<PlayerDetails>
}
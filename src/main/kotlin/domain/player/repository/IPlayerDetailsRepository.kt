package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerDetails
import java.util.Optional

interface IPlayerDetailsRepository {
    suspend fun save(data: PlayerDetails): PlayerDetails

    suspend fun findById(id: String): Optional<PlayerDetails>
}
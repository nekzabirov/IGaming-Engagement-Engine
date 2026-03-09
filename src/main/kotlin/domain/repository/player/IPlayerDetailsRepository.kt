package com.nekgambling.domain.repository.player

import com.nekgambling.domain.model.player.PlayerDetails
import java.util.Optional

interface IPlayerDetailsRepository {
    suspend fun save(data: PlayerDetails): PlayerDetails

    suspend fun findById(id: String): Optional<PlayerDetails>
}
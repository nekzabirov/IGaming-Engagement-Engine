package com.nekgambling.domain.repository.player

import com.nekgambling.domain.model.player.PlayerSpin
import java.util.Optional

interface IPlayerSpinRepository {
    suspend fun save(spin: PlayerSpin): PlayerSpin

    suspend fun findBy(playerId: String, spinId: String, game: String): Optional<PlayerSpin>
}
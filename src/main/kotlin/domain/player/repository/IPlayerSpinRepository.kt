package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerSpin
import java.util.Optional

interface IPlayerSpinRepository {
    suspend fun save(spin: PlayerSpin): PlayerSpin

    suspend fun findBy(playerId: String, spinId: String, game: String): Optional<PlayerSpin>
}
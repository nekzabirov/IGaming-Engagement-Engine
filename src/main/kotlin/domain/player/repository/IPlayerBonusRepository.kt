package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerBonus
import java.util.Optional

interface IPlayerBonusRepository {
    suspend fun save(playerBonus: PlayerBonus): PlayerBonus
    suspend fun findById(id: String): Optional<PlayerBonus>
}

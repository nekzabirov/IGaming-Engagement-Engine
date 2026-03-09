package com.nekgambling.domain.repository.player

import com.nekgambling.domain.model.player.PlayerBonus
import java.util.Optional

interface IPlayerBonusRepository {
    suspend fun save(playerBonus: PlayerBonus): PlayerBonus
    suspend fun findById(id: String): Optional<PlayerBonus>
}

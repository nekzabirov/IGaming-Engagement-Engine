package com.nekgambling.domain.repository.player

import com.nekgambling.domain.model.player.PlayerFreespin
import java.util.Optional

interface IPlayerFreespinRepository {
    suspend fun save(playerFreespin: PlayerFreespin): PlayerFreespin
    suspend fun findBy(playerId: String, freespinId: String): Optional<PlayerFreespin>
}
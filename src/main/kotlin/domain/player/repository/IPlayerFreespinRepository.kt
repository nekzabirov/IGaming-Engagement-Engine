package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerFreespin
import java.util.Optional

interface IPlayerFreespinRepository {
    suspend fun save(playerFreespin: PlayerFreespin): PlayerFreespin
    suspend fun findBy(playerId: String, freespinId: String): Optional<PlayerFreespin>
}
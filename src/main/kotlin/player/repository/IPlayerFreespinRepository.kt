package com.nekgambling.player.repository

import com.nekgambling.player.model.PlayerFreespin
import java.util.Optional

interface IPlayerFreespinRepository {
    suspend fun save(playerFreespin: PlayerFreespin): PlayerFreespin
    suspend fun findBy(playerId: String, freespinId: String): Optional<PlayerFreespin>
}
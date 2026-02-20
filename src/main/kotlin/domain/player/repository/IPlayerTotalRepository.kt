package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerTotalSpinInfo
import com.nekgambling.domain.vo.Period

interface IPlayerTotalRepository {
    suspend fun readPlayerTotalSpin(playerId: String, period: Period): PlayerTotalSpinInfo
}
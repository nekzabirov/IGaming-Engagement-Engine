package com.nekgambling.application.reader

import com.nekgambling.domain.player.model.PlayerTotalSpinInfo
import com.nekgambling.domain.vo.Period

interface IPlayerSpinTotalReader {

    suspend fun read(playerId: String, period: Period): PlayerTotalSpinInfo

}
package com.nekgambling.application.reader

import com.nekgambling.domain.vo.Period

interface IPlayerBonusPayoutTotalReader {

    suspend fun read(playerId: String, period: Period): Long

}
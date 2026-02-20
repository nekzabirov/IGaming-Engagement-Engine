package com.nekgambling.infrastructure.clickhouse.reader

import com.nekgambling.application.reader.IPlayerBonusPayoutTotalReader
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.clickhouse.ClickHouseTable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ClickHousePlayerBonusPayoutTotalReader(private val client: ClickHouseClient) : IPlayerBonusPayoutTotalReader {

    override suspend fun read(playerId: String, period: Period): Long = coroutineScope {
        val bonusPayoutDeferred = async {
            client.queryOne(
                """
                SELECT sum(payout_amount) AS total
                FROM ${ClickHouseTable.PLAYER_BONUS} FINAL
                WHERE player_id = ?
                """.trimIndent(),
                listOf(playerId),
            ) { rs -> rs.getLong("total") } ?: 0L
        }

        val freespinPayoutDeferred = async {
            client.queryOne(
                """
                SELECT sum(payout_real_amount) AS total
                FROM ${ClickHouseTable.PLAYER_FREESPIN} FINAL
                WHERE player_id = ?
                """.trimIndent(),
                listOf(playerId),
            ) { rs -> rs.getLong("total") } ?: 0L
        }

        bonusPayoutDeferred.await() + freespinPayoutDeferred.await()
    }

}

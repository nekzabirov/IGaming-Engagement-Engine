package com.nekgambling.infrastructure.clickhouse.reader

import com.nekgambling.application.reader.IPlayerSpinTotalReader
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient

class ClickHousePlayerSpinTotalReader(private val client: ClickHouseClient) : IPlayerSpinTotalReader {

    override suspend fun read(playerId: String, period: Period): IPlayerSpinTotalReader.Result {
        val result = client.queryOne(
            """
            SELECT
                sum(placeAmount) AS placeAmount,
                sum(settleAmount) AS settleAmount,
                sum(realPlaceAmount) AS realPlaceAmount,
                sum(realSettleAmount) AS realSettleAmount
            FROM player_total_spin_info
            WHERE player_id = ? AND date >= toDate(?) AND date <= toDate(?)
            """.trimIndent(),
            listOf(
                playerId,
                java.time.Instant.ofEpochMilli(period.first.toEpochMilliseconds()),
                java.time.Instant.ofEpochMilli(period.second.toEpochMilliseconds()),
            ),
        ) { rs ->
            IPlayerSpinTotalReader.Result(
                placeAmount = rs.getLong("placeAmount"),
                settleAmount = rs.getLong("settleAmount"),
                realPlaceAmount = rs.getLong("realPlaceAmount"),
                realSettleAmount = rs.getLong("realSettleAmount"),
            )
        }

        return result ?: IPlayerSpinTotalReader.Result(
            placeAmount = 0,
            settleAmount = 0,
            realPlaceAmount = 0,
            realSettleAmount = 0,
        )
    }

}
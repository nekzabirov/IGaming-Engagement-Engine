package com.nekgambling.infrastructure.database.clickhouse.query

import com.nekgambling.application.query.IQueryHandler
import com.nekgambling.application.query.player.GetPlayerSpinTotalQuery
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import kotlin.reflect.KClass

class ClickHousePlayerSpinTotalQueryHandler(
    private val client: ClickHouseClient,
) : IQueryHandler<GetPlayerSpinTotalQuery, GetPlayerSpinTotalQuery.Result> {

    override val queryType: KClass<GetPlayerSpinTotalQuery> = GetPlayerSpinTotalQuery::class

    override suspend fun handle(query: GetPlayerSpinTotalQuery): GetPlayerSpinTotalQuery.Result {
        val result = client.queryOne(
            """
            SELECT
                sum(placeAmount) AS placeAmount,
                sum(settleAmount) AS settleAmount,
                sum(realPlaceAmount) AS realPlaceAmount,
                sum(realSettleAmount) AS realSettleAmount
            FROM ${ClickHouseTable.PLAYER_TOTAL_SPIN_INFO}
            WHERE player_id = ? AND date >= toDate(?) AND date <= toDate(?)
            """.trimIndent(),
            listOf(
                query.playerId,
                java.time.Instant.ofEpochMilli(query.period.first.toEpochMilliseconds()).atZone(java.time.ZoneOffset.UTC).toLocalDate().toString(),
                java.time.Instant.ofEpochMilli(query.period.second.toEpochMilliseconds()).atZone(java.time.ZoneOffset.UTC).toLocalDate().toString(),
            ),
        ) { rs ->
            GetPlayerSpinTotalQuery.Result(
                placeAmount = rs.getLong("placeAmount"),
                settleAmount = rs.getLong("settleAmount"),
                realPlaceAmount = rs.getLong("realPlaceAmount"),
                realSettleAmount = rs.getLong("realSettleAmount"),
            )
        }

        return result ?: GetPlayerSpinTotalQuery.Result(
            placeAmount = 0,
            settleAmount = 0,
            realPlaceAmount = 0,
            realSettleAmount = 0,
        )
    }
}

package com.nekgambling.infrastructure.database.clickhouse.query

import com.nekgambling.application.query.IQueryHandler
import com.nekgambling.application.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass

class ClickHousePlayerBonusPayoutTotalQueryHandler(
    private val client: ClickHouseClient,
) : IQueryHandler<GetPlayerBonusPayoutTotalQuery, Long> {

    override val queryType: KClass<GetPlayerBonusPayoutTotalQuery> = GetPlayerBonusPayoutTotalQuery::class

    override suspend fun handle(query: GetPlayerBonusPayoutTotalQuery): Long = coroutineScope {
        val bonusPayoutDeferred = async {
            client.queryOne(
                """
                SELECT sum(payout_amount) AS total
                FROM ${ClickHouseTable.PLAYER_BONUS} FINAL
                WHERE player_id = ?
                """.trimIndent(),
                listOf(query.playerId),
            ) { rs -> rs.getLong("total") } ?: 0L
        }

        val freespinPayoutDeferred = async {
            client.queryOne(
                """
                SELECT sum(payout_real_amount) AS total
                FROM ${ClickHouseTable.PLAYER_FREESPIN} FINAL
                WHERE player_id = ?
                """.trimIndent(),
                listOf(query.playerId),
            ) { rs -> rs.getLong("total") } ?: 0L
        }

        bonusPayoutDeferred.await() + freespinPayoutDeferred.await()
    }
}

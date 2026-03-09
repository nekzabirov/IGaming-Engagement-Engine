package com.nekgambling.infrastructure.database.clickhouse.query

import com.nekgambling.application.query.IQueryHandler
import com.nekgambling.application.query.player.GetPlayerInvoiceTotalQuery
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import kotlin.reflect.KClass

class ClickHousePlayerInvoiceTotalQueryHandler(
    private val client: ClickHouseClient,
) : IQueryHandler<GetPlayerInvoiceTotalQuery, GetPlayerInvoiceTotalQuery.Result> {

    override val queryType: KClass<GetPlayerInvoiceTotalQuery> = GetPlayerInvoiceTotalQuery::class

    override suspend fun handle(query: GetPlayerInvoiceTotalQuery): GetPlayerInvoiceTotalQuery.Result {
        val result = client.queryOne(
            """
            SELECT
                sum(depositAmount) AS depositAmount,
                sum(withdrawAmount) AS withdrawAmount,
                sum(depositCount) AS depositCount,
                sum(withdrawCount) AS withdrawCount,
                sum(taxAmount) AS taxAmount,
                sum(feesAmount) AS feesAmount
            FROM ${ClickHouseTable.PLAYER_INVOICE_TOTAL}
            WHERE player_id = ? AND date >= toDate(?) AND date <= toDate(?)
            """.trimIndent(),
            listOf(
                query.playerId,
                java.time.Instant.ofEpochMilli(query.period.first.toEpochMilliseconds()).atZone(java.time.ZoneOffset.UTC).toLocalDate().toString(),
                java.time.Instant.ofEpochMilli(query.period.second.toEpochMilliseconds()).atZone(java.time.ZoneOffset.UTC).toLocalDate().toString(),
            ),
        ) { rs ->
            GetPlayerInvoiceTotalQuery.Result(
                depositAmount = rs.getLong("depositAmount"),
                withdrawAmount = rs.getLong("withdrawAmount"),
                depositCount = rs.getInt("depositCount"),
                withdrawCount = rs.getInt("withdrawCount"),
                taxAmount = rs.getLong("taxAmount"),
                feesAmount = rs.getLong("feesAmount"),
            )
        }

        return result ?: GetPlayerInvoiceTotalQuery.Result(
            depositAmount = 0,
            withdrawAmount = 0,
            depositCount = 0,
            withdrawCount = 0,
            taxAmount = 0,
            feesAmount = 0,
        )
    }
}

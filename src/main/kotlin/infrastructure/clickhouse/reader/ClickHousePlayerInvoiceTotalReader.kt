package com.nekgambling.infrastructure.clickhouse.reader

import com.nekgambling.application.reader.IPlayerInvoiceTotalReader
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient

class ClickHousePlayerInvoiceTotalReader(private val client: ClickHouseClient) : IPlayerInvoiceTotalReader {

    override suspend fun read(playerId: String, period: Period): IPlayerInvoiceTotalReader.Result {
        val result = client.queryOne(
            """
            SELECT
                sum(depositAmount) AS depositAmount,
                sum(withdrawAmount) AS withdrawAmount,
                sum(depositCount) AS depositCount,
                sum(withdrawCount) AS withdrawCount,
                sum(taxAmount) AS taxAmount,
                sum(feesAmount) AS feesAmount
            FROM player_invoice_total
            WHERE player_id = ? AND date >= toDate(?) AND date <= toDate(?)
            """.trimIndent(),
            listOf(
                playerId,
                java.time.Instant.ofEpochMilli(period.first.toEpochMilliseconds()),
                java.time.Instant.ofEpochMilli(period.second.toEpochMilliseconds()),
            ),
        ) { rs ->
            IPlayerInvoiceTotalReader.Result(
                depositAmount = rs.getLong("depositAmount"),
                withdrawAmount = rs.getLong("withdrawAmount"),
                depositCount = rs.getInt("depositCount"),
                withdrawCount = rs.getInt("withdrawCount"),
                taxAmount = rs.getLong("taxAmount"),
                feesAmount = rs.getLong("feesAmount"),
            )
        }

        return result ?: IPlayerInvoiceTotalReader.Result(
            depositAmount = 0,
            withdrawAmount = 0,
            depositCount = 0,
            withdrawCount = 0,
            taxAmount = 0,
            feesAmount = 0,
        )
    }

}

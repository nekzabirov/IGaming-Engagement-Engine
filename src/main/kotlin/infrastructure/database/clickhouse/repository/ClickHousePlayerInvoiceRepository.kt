package com.nekgambling.infrastructure.database.clickhouse.repository

import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.repository.player.IPlayerInvoiceRepository
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import kotlinx.datetime.Instant
import java.sql.ResultSet
import java.util.Optional

class ClickHousePlayerInvoiceRepository(
    private val client: ClickHouseClient,
) : IPlayerInvoiceRepository {

    override suspend fun findById(id: String): Optional<PlayerInvoice> {
        val result = client.queryOne(
            "SELECT * FROM ${ClickHouseTable.PLAYER_INVOICE} FINAL WHERE id = ?",
            listOf(id),
            ::mapRow,
        )
        return Optional.ofNullable(result)
    }

    override suspend fun save(data: PlayerInvoice): PlayerInvoice {
        client.execute(
            """
            INSERT INTO ${ClickHouseTable.PLAYER_INVOICE} (
                id, player_id, type, status, transaction_currency,
                amount, transaction_amount, tax_amount, fee_amount, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            listOf(
                data.id,
                data.playerId,
                data.type.name,
                data.status.name,
                data.transactionCurrency.code,
                data.amount,
                data.transactionAmount,
                data.taxAmount,
                data.feeAmount,
                java.time.Instant.ofEpochMilli(data.createdAt.toEpochMilliseconds()),
            )
        )
        return data
    }

    private fun mapRow(rs: ResultSet): PlayerInvoice {
        return PlayerInvoice(
            id = rs.getString("id"),
            playerId = rs.getString("player_id"),
            type = PlayerInvoice.Type.valueOf(rs.getString("type")),
            status = PlayerInvoice.Status.valueOf(rs.getString("status")),
            transactionCurrency = Currency(rs.getString("transaction_currency")),
            amount = rs.getLong("amount"),
            transactionAmount = rs.getLong("transaction_amount"),
            taxAmount = rs.getLong("tax_amount"),
            feeAmount = rs.getLong("fee_amount"),
            createdAt = Instant.fromEpochMilliseconds(rs.getTimestamp("created_at").time),
        )
    }
}

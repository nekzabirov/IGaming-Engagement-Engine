package com.nekgambling.infrastructure.database.clickhouse.repository

import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.repository.player.IPlayerBonusRepository
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import java.sql.ResultSet
import java.util.Optional

class ClickHousePlayerBonusRepository(
    private val client: ClickHouseClient,
) : IPlayerBonusRepository {

    override suspend fun save(playerBonus: PlayerBonus): PlayerBonus {
        client.execute(
            """
            INSERT INTO ${ClickHouseTable.PLAYER_BONUS} (id, identity, player_id, status, amount, payout_amount)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            listOf(
                playerBonus.id,
                playerBonus.identity,
                playerBonus.playerId,
                playerBonus.status.name,
                playerBonus.amount,
                playerBonus.payoutAmount,
            )
        )
        return playerBonus
    }

    override suspend fun findById(id: String): Optional<PlayerBonus> {
        val result = client.queryOne(
            "SELECT * FROM ${ClickHouseTable.PLAYER_BONUS} FINAL WHERE id = ?",
            listOf(id),
            ::mapRow,
        )
        return Optional.ofNullable(result)
    }

    private fun mapRow(rs: ResultSet): PlayerBonus {
        return PlayerBonus(
            id = rs.getString("id"),
            identity = rs.getString("identity"),
            playerId = rs.getString("player_id"),
            status = PlayerBonus.Status.valueOf(rs.getString("status")),
            amount = rs.getLong("amount"),
            payoutAmount = rs.getLong("payout_amount"),
        )
    }
}

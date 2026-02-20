package com.nekgambling.infrastructure.clickhouse.repository

import com.nekgambling.domain.player.model.PlayerSpin
import com.nekgambling.domain.player.repository.IPlayerSpinRepository
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.clickhouse.ClickHouseTable
import kotlinx.datetime.Instant
import java.sql.ResultSet
import java.util.Optional

class ClickHousePlayerSpinRepository(
    private val client: ClickHouseClient,
) : IPlayerSpinRepository {

    override suspend fun save(spin: PlayerSpin): PlayerSpin {
        client.execute(
            """
            INSERT INTO ${ClickHouseTable.PLAYER_SPIN} (
                id, player_id, freespin_id, spin_currency, game,
                place_real_amount, settle_real_amount,
                place_bonus_amount, settle_bonus_amount, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            listOf(
                spin.id,
                spin.playerId,
                spin.freespinId,
                spin.spinCurrency.code,
                spin.game,
                spin.placeRealAmount,
                spin.settleRealAmount,
                spin.placeBonusAmount,
                spin.settleBonusAmount,
                java.time.Instant.ofEpochMilli(spin.createdAt.toEpochMilliseconds()),
            )
        )
        return spin
    }

    override suspend fun findBy(playerId: String, spinId: String, game: String): Optional<PlayerSpin> {
        val result = client.queryOne(
            "SELECT * FROM ${ClickHouseTable.PLAYER_SPIN} FINAL WHERE player_id = ? AND id = ? AND game = ?",
            listOf(playerId, spinId, game),
            ::mapRow,
        )
        return Optional.ofNullable(result)
    }

    private fun mapRow(rs: ResultSet): PlayerSpin {
        return PlayerSpin(
            id = rs.getString("id"),
            playerId = rs.getString("player_id"),
            freespinId = rs.getString("freespin_id"),
            spinCurrency = Currency(rs.getString("spin_currency")),
            game = rs.getString("game"),
            placeRealAmount = rs.getLong("place_real_amount"),
            settleRealAmount = rs.getLong("settle_real_amount"),
            placeBonusAmount = rs.getLong("place_bonus_amount"),
            settleBonusAmount = rs.getLong("settle_bonus_amount"),
            createdAt = Instant.fromEpochMilliseconds(rs.getTimestamp("created_at").time),
        )
    }
}

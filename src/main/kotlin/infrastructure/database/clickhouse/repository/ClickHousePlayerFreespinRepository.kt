package com.nekgambling.infrastructure.database.clickhouse.repository

import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.repository.player.IPlayerFreespinRepository
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseTable
import java.sql.ResultSet
import java.util.Optional

class ClickHousePlayerFreespinRepository(
    private val client: ClickHouseClient,
) : IPlayerFreespinRepository {

    override suspend fun save(playerFreespin: PlayerFreespin): PlayerFreespin {
        client.execute(
            """
            INSERT INTO ${ClickHouseTable.PLAYER_FREESPIN} (id, identity, player_id, game, status, payout_real_amount)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            listOf(
                playerFreespin.id,
                playerFreespin.identity,
                playerFreespin.playerId,
                playerFreespin.game,
                playerFreespin.status.name,
                playerFreespin.payoutRealAmount,
            )
        )
        return playerFreespin
    }

    override suspend fun findBy(playerId: String, freespinId: String): Optional<PlayerFreespin> {
        val result = client.queryOne(
            "SELECT * FROM ${ClickHouseTable.PLAYER_FREESPIN} FINAL WHERE player_id = ? AND id = ?",
            listOf(playerId, freespinId),
            ::mapRow,
        )
        return Optional.ofNullable(result)
    }

    private fun mapRow(rs: ResultSet): PlayerFreespin {
        return PlayerFreespin(
            id = rs.getString("id"),
            identity = rs.getString("identity"),
            playerId = rs.getString("player_id"),
            game = rs.getString("game"),
            status = PlayerFreespin.Status.valueOf(rs.getString("status")),
            payoutRealAmount = rs.getLong("payout_real_amount"),
        )
    }
}

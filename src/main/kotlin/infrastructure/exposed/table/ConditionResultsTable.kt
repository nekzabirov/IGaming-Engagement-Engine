package com.nekgambling.infrastructure.exposed.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ConditionResultsTable : Table("condition_results") {
    val playerId = varchar("player_id", 255)
    val condition = reference("condition_id", ConditionsTable)
    val passed = bool("passed")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(playerId, condition)
}

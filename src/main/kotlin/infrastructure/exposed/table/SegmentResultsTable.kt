package com.nekgambling.infrastructure.exposed.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SegmentResultsTable : Table("segment_results") {
    val playerId = varchar("player_id", 255)
    val segmentId = integer("segment_id")
    val passed = bool("passed")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(playerId, segmentId)
}

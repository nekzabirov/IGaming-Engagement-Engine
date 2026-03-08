package com.nekgambling.infrastructure.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable

object JourneyInstantsTable : LongIdTable("journey_instants") {
    val playerId = varchar("player_id", 255)
    val journey = reference("journey_id", JourneysTable)
    val currentNode = reference("current_node_id", JourneyNodesTable)
    val payload = text("payload").default("{}")

    init {
        uniqueIndex(playerId, journey)
    }
}

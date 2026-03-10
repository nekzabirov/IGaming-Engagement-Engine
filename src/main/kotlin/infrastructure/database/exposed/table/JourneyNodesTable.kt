package com.nekgambling.infrastructure.database.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable

object JourneyNodesTable : LongIdTable("journey_nodes") {
    val type = varchar("type", 255)
    val journeyId = reference("journey_id", JourneysTable)
    val next = optReference("next_id", JourneyNodesTable)
}

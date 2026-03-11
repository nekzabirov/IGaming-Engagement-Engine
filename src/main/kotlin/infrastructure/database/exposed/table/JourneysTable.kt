package com.nekgambling.infrastructure.database.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable

object JourneysTable : LongIdTable("journeys") {
    val identity = varchar("identity", 255)
    val head = optReference("head_id", JourneyNodesTable)
}

package com.nekgambling.infrastructure.database.exposed.entity

import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JourneyNodeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<JourneyNodeEntity>(JourneyNodesTable)

    var type by JourneyNodesTable.type
    var journey by JourneyEntity referencedOn JourneyNodesTable.journeyId
    var next by JourneyNodeEntity optionalReferencedOn JourneyNodesTable.next
}

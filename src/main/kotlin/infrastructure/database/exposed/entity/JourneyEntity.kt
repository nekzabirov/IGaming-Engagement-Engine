package com.nekgambling.infrastructure.database.exposed.entity

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.infrastructure.database.exposed.table.JourneysTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JourneyEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<JourneyEntity>(JourneysTable)

    var identity by JourneysTable.identity
    var head by JourneyNodeEntity referencedOn JourneysTable.head

    fun toDomain() = Journey(
        id = id.value,
        identity = identity,
        head = head.toDomain(),
    )
}

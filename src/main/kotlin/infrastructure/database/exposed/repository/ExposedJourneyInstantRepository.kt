package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.model.journey.JourneyInstant
import com.nekgambling.domain.repository.IJourneyInstantRepository
import com.nekgambling.infrastructure.database.exposed.entity.JourneyEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyInstantEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.entity.serializePayload
import com.nekgambling.infrastructure.database.exposed.table.JourneyInstantsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedJourneyInstantRepository(
    private val database: Database,
) : IJourneyInstantRepository {

    override suspend fun findBy(playerId: String, journey: Journey): JourneyInstant? =
        newSuspendedTransaction(db = database) {
            require(journey.id != Long.MIN_VALUE) { "Cannot find instant for unsaved journey" }

            JourneyInstantEntity.find {
                (JourneyInstantsTable.playerId eq playerId) and
                    (JourneyInstantsTable.journey eq journey.id)
            }.firstOrNull()?.toDomain()
        }

    override suspend fun save(journeyInstant: JourneyInstant): JourneyInstant =
        newSuspendedTransaction(db = database) {
            val journey = journeyInstant.journey
            val currentNode = journeyInstant.currentNode
            require(journey.id != Long.MIN_VALUE) { "Cannot save instant for unsaved journey" }
            require(currentNode.id != Long.MIN_VALUE) { "Cannot save instant for unsaved node" }

            val entity = if (journeyInstant.id != Long.MIN_VALUE) {
                JourneyInstantEntity.findById(journeyInstant.id)
                    ?: error("JourneyInstant not found: ${journeyInstant.id}")
            } else {
                JourneyInstantEntity.new {}
            }

            entity.playerId = journeyInstant.playerId
            entity.journey = JourneyEntity.findById(journey.id)
                ?: error("Journey not found: ${journey.id}")
            entity.currentNode = JourneyNodeEntity.findById(currentNode.id)
                ?: error("JourneyNode not found: ${currentNode.id}")
            entity.payload = serializePayload(journeyInstant.payload)

            entity.toDomain()
        }
}

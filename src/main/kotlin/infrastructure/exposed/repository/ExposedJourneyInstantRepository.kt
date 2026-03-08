package com.nekgambling.infrastructure.exposed.repository

import com.nekgambling.domain.journey.model.Journey
import com.nekgambling.domain.journey.model.JourneyInstant
import com.nekgambling.domain.journey.repository.IJourneyInstantRepository
import com.nekgambling.infrastructure.exposed.entity.JourneyInstantEntity
import com.nekgambling.infrastructure.exposed.entity.serializePayload
import com.nekgambling.infrastructure.exposed.table.JourneyInstantsTable
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
            entity.journey = com.nekgambling.infrastructure.exposed.entity.JourneyEntity.findById(journey.id)
                ?: error("Journey not found: ${journey.id}")
            entity.currentNode = com.nekgambling.infrastructure.exposed.entity.JourneyNodeEntity.findById(currentNode.id)
                ?: error("JourneyNode not found: ${currentNode.id}")
            entity.payload = serializePayload(journeyInstant.payload)

            entity.toDomain()
        }
}

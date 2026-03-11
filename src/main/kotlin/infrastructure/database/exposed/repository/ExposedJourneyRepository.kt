package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.infrastructure.database.exposed.entity.JourneyEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyMapper
import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedJourneyRepository(
    private val database: Database,
    private val journeyMapper: JourneyMapper,
) : IJourneyRepository {

    override suspend fun findOfNode(node: IJourneyNode): Journey {
        return newSuspendedTransaction(db = database) {
            val nodeEntity = JourneyNodeEntity[node.id]
            val journeyEntity = nodeEntity.journey
            journeyMapper.toDomain(journeyEntity)
        }
    }
}

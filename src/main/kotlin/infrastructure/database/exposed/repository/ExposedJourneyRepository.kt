package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedJourneyRepository(
    private val database: Database,
) : IJourneyRepository {

    override suspend fun findOfNode(node: IJourneyNode): Journey = newSuspendedTransaction(db = database) {
        require(node.id != Long.MIN_VALUE) { "Cannot find journey for unsaved node" }

        val nodeEntity = JourneyNodeEntity.findById(node.id)
            ?: error("Journey node not found: ${node.id}")

        nodeEntity.journey.toDomain()
    }
}

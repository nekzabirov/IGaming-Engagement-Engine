package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import org.jetbrains.exposed.sql.Database

class ExposedJourneyRepository(
    private val database: Database,
) : IJourneyRepository {

    override suspend fun findOfNode(node: IJourneyNode): Journey {
        TODO("Journey node DB mapping pending rebuild")
    }
}

package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.model.journey.JourneyInstant
import com.nekgambling.domain.repository.IJourneyInstantRepository
import org.jetbrains.exposed.sql.Database
import java.util.Optional

class ExposedJourneyInstantRepository(
    private val database: Database,
) : IJourneyInstantRepository {

    override suspend fun findBy(playerId: String, journey: Journey): Optional<JourneyInstant> {
        TODO("Journey node DB mapping pending rebuild")
    }

    override suspend fun save(journeyInstant: JourneyInstant): JourneyInstant {
        TODO("Journey node DB mapping pending rebuild")
    }

    override suspend fun delete(journeyInstant: JourneyInstant) {
        TODO("Journey node DB mapping pending rebuild")
    }
}

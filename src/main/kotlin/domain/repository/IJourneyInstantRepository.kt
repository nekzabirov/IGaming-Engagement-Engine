package com.nekgambling.domain.repository

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.model.journey.JourneyInstant
import java.util.Optional

interface IJourneyInstantRepository {

    suspend fun findBy(playerId: String, journey: Journey): Optional<JourneyInstant>

    suspend fun save(journeyInstant: JourneyInstant): JourneyInstant

    suspend fun delete(journeyInstant: JourneyInstant)
}

package com.nekgambling.domain.journey.repository

import com.nekgambling.domain.journey.model.Journey
import com.nekgambling.domain.journey.model.JourneyInstant

interface IJourneyInstantRepository {

    suspend fun findBy(playerId: String, journey: Journey): JourneyInstant?

    suspend fun save(journeyInstant: JourneyInstant): JourneyInstant

}

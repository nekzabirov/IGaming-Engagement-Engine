package com.nekgambling.domain.repository

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.model.journey.JourneyInstant

interface IJourneyInstantRepository {

    suspend fun findBy(playerId: String, journey: Journey): JourneyInstant?

    suspend fun save(journeyInstant: JourneyInstant): JourneyInstant

}

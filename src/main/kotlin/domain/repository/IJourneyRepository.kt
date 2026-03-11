package com.nekgambling.domain.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey

interface IJourneyRepository {

    suspend fun findOfNode(node: IJourneyNode): Journey

    suspend fun findById(id: Long): Journey?

    suspend fun findAll(): List<Journey>

    suspend fun save(journey: Journey): Journey

    suspend fun delete(id: Long)

}

package com.nekgambling.domain.journey.repository

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.journey.model.Journey

interface IJourneyRepository {

    suspend fun findOfNode(node: IJourneyNode): Journey

}
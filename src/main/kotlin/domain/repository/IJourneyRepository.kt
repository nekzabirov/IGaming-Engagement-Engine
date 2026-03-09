package com.nekgambling.domain.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey

interface IJourneyRepository {

    suspend fun findOfNode(node: IJourneyNode): Journey

}

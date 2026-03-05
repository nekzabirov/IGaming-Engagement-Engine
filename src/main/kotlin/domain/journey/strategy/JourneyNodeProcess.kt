package com.nekgambling.domain.journey.strategy

import com.nekgambling.domain.journey.model.IJourneyNode
import kotlin.reflect.KClass

interface JourneyNodeProcess<N: IJourneyNode> {
    val nodeType: KClass<N>

    suspend fun process(playerId: String, node: N, payload: Map<String, Any>) : Response?

    data class Response(
        val nextNode: IJourneyNode?,
        val output: Map<String, Any>,
    )
}
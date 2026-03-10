package com.nekgambling.domain.strategy

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Payload
import kotlin.reflect.KClass

interface JourneyNodeProcess<N: IJourneyNode> {
    val nodeType: KClass<N>

    suspend fun process(playerId: String, node: N, payload: Payload) : Response?

    data class Response(
        val nextNode: IJourneyNode?,
        val output: Payload,
    )
}
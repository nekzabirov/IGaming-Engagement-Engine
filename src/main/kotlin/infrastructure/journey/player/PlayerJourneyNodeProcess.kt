package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.strategy.JourneyNodeProcess
import kotlin.reflect.KClass

interface IPlayerDefinitionEvaluator<T : IPlayerDefinition> {
    val definition: KClass<T>

    suspend fun evaluate(playerId: String, condition: T): Boolean
}

class PlayerJourneyNodeProcess(private val evaluators: List<IPlayerDefinitionEvaluator<*>>): JourneyNodeProcess<PlayerJourneyNode> {
    override val nodeType: KClass<PlayerJourneyNode> = PlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: PlayerJourneyNode,
        payload: Map<String, Any>
    ): JourneyNodeProcess.Response? {
        TODO("Not yet implemented")
    }
}
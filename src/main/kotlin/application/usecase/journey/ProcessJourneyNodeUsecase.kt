package com.nekgambling.application.usecase.journey

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.journey.repository.IJourneyRepository
import com.nekgambling.domain.journey.strategy.JourneyNodeProcessResolver

class ProcessJourneyNodeUsecase(
    private val journeyRepository: IJourneyRepository,
    private val journeyNodeProcessResolver: JourneyNodeProcessResolver
)  {

    suspend operator fun invoke(playerId: String, input: Map<String, Any>, node: IJourneyNode) {
        val journey = journeyRepository.findOfNode(node)

        val payload = mutableMapOf<String, Any>().apply { putAll(input) }
        var currentNode: IJourneyNode? = node

        while (currentNode != null) {
            val result = journeyNodeProcessResolver.process(playerId = playerId, node = currentNode, payload = payload)

            currentNode = result?.nextNode
            result?.output?.let { payload.putAll(it) }
        }


    }

}
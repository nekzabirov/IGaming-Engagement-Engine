package com.nekgambling.application.usecase.journey

import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.application.resolver.JourneyNodeProcessResolver
import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.JourneyInstant
import com.nekgambling.domain.repository.IJourneyInstantRepository
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.domain.vo.Payload
import kotlin.jvm.optionals.getOrNull

class ProcessJourneyNodeUsecase(
    private val journeyRepository: IJourneyRepository,
    private val journeyInstantRepository: IJourneyInstantRepository,
    private val lockAdapter: ILockAdapter,
    private val journeyNodeProcessResolver: JourneyNodeProcessResolver
) {
    suspend operator fun invoke(playerId: String, node: IJourneyNode, payload: Payload): Result<Unit> =
        runCatching {
            val journey = journeyRepository.findOfNode(node)

            lockAdapter.withLock("journey:${journey.id}:playerId:$playerId") {
                var instant = journeyInstantRepository.findBy(playerId = playerId, journey = journey).getOrNull()

                if (instant == null && journey.head == node) {
                    instant =
                        JourneyInstant(playerId = playerId, journey = journey, currentNode = node, payload = payload)
                }

                if (instant?.currentNode != node) return@withLock

                val updatedPayload = instant.payload.toMutableMap().apply {
                    putAll(payload)
                }

                process(instant.copy(payload = updatedPayload))
            }
        }

    private suspend fun process(instant: JourneyInstant) {
        val node: IJourneyNode = instant.currentNode
        val payload = instant.payload.toMutableMap()

        val response = journeyNodeProcessResolver.process(playerId = instant.playerId, node = node, payload = payload) ?: return

        payload.putAll(response.output)

        val nextNode = response.nextNode

        if (nextNode == null) {
            journeyInstantRepository.delete(instant)
            return
        }

        val updatedInstant = journeyInstantRepository.save(instant.copy(currentNode = nextNode, payload = payload))

        process(updatedInstant)
    }
}
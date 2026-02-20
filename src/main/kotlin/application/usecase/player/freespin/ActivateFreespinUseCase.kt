package com.nekgambling.application.usecase.player.freespin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.freespin.FreespinActivatedEvent
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.player.repository.IPlayerFreespinRepository

class ActivateFreespinUseCase(
    private val playerFreespinRepository: IPlayerFreespinRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val freespin = playerFreespinRepository.findBy(command.playerId, command.freespinId)
            .orElseThrow { NoSuchElementException("Freespin not found") }

        val updated = freespin.copy(status = PlayerFreespin.Status.ACTIVE)

        playerFreespinRepository.save(updated)

        eventAdapter.publish(FreespinActivatedEvent(updated))
    }

    data class Command(
        val playerId: String,

        val freespinId: String,
    )
}

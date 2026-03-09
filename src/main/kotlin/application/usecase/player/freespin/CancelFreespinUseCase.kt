package com.nekgambling.application.usecase.player.freespin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.freespin.FreespinCanceledEvent
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.repository.player.IPlayerFreespinRepository

class CancelFreespinUseCase(
    private val playerFreespinRepository: IPlayerFreespinRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val freespin = playerFreespinRepository.findBy(command.playerId, command.freespinId)
            .orElseThrow { NoSuchElementException("Freespin not found") }

        val updated = freespin.copy(status = PlayerFreespin.Status.CANCELLED)

        playerFreespinRepository.save(updated)

        eventAdapter.publish(FreespinCanceledEvent(updated))
    }

    data class Command(
        val playerId: String,

        val freespinId: String,
    )
}

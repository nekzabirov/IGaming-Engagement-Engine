package com.nekgambling.player.usecase.freespin

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.event.freespin.FreespinPlayedEvent
import com.nekgambling.player.model.PlayerFreespin
import com.nekgambling.player.repository.IPlayerFreespinRepository

class PlayFreespinUsecase(
    private val playerFreespinRepository: IPlayerFreespinRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val freespin = playerFreespinRepository.findBy(command.playerId, command.freespinId)
            .orElseThrow { NoSuchElementException("Freespin not found") }

        val updated = freespin.copy(
            status = PlayerFreespin.Status.PLAYED,
            payoutRealAmount = command.payoutRealAmount,
        )

        playerFreespinRepository.save(updated)

        eventAdapter.publish(FreespinPlayedEvent(updated))
    }

    data class Command(
        val playerId: String,

        val freespinId: String,

        val payoutRealAmount: Long,
    )
}

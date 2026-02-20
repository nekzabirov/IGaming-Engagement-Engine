package com.nekgambling.player.usecase.freespin

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.event.freespin.FreespinIssuedEvent
import com.nekgambling.player.model.PlayerFreespin
import com.nekgambling.player.repository.IPlayerFreespinRepository

class IssueFreespinUseCase(
    private val playerFreespinRepository: IPlayerFreespinRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val freespin = PlayerFreespin(
            id = command.id,

            identity = command.identity,

            playerId = command.playerId,

            game = command.game,

            status = PlayerFreespin.Status.ISSUE,

            payoutRealAmount = 0L,
        )

        playerFreespinRepository.save(freespin)

        eventAdapter.publish(FreespinIssuedEvent(freespin))
    }

    data class Command(
        val id: String,

        val identity: String,

        val playerId: String,

        val game: String,
    )
}

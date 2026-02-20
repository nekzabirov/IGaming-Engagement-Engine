package com.nekgambling.application.usecase.player.freespin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.freespin.FreespinIssuedEvent
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.player.repository.IPlayerFreespinRepository

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

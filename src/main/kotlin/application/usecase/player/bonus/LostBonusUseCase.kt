package com.nekgambling.application.usecase.player.bonus

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.bonus.BonusLostEvent
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.repository.player.IPlayerBonusRepository

class LostBonusUseCase(
    private val playerBonusRepository: IPlayerBonusRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val bonus = playerBonusRepository.findById(command.id)
            .orElseThrow { NoSuchElementException("Bonus not found") }

        val updated = bonus.copy(status = PlayerBonus.Status.LOST)

        playerBonusRepository.save(updated)

        eventAdapter.publish(BonusLostEvent(updated))
    }

    data class Command(
        val id: String,
    )
}

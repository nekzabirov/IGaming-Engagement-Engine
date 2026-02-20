package com.nekgambling.player.usecase.bonus

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.event.bonus.BonusLostEvent
import com.nekgambling.player.model.PlayerBonus
import com.nekgambling.player.repository.IPlayerBonusRepository

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

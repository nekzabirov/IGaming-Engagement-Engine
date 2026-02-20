package com.nekgambling.application.usecase.player.bonus

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.bonus.BonusStartWageringEvent
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.player.repository.IPlayerBonusRepository

class StartWageringBonusUseCase(
    private val playerBonusRepository: IPlayerBonusRepository,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val bonus = playerBonusRepository.findById(command.id)
            .orElseThrow { NoSuchElementException("Bonus not found") }

        val updated = bonus.copy(status = PlayerBonus.Status.WAGERING)

        playerBonusRepository.save(updated)

        eventAdapter.publish(BonusStartWageringEvent(updated))
    }

    data class Command(
        val id: String,
    )
}

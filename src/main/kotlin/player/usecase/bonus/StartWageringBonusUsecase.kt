package com.nekgambling.player.usecase.bonus

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.event.bonus.BonusStartWageringEvent
import com.nekgambling.player.model.PlayerBonus
import com.nekgambling.player.repository.IPlayerBonusRepository

class StartWageringBonusUsecase(
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

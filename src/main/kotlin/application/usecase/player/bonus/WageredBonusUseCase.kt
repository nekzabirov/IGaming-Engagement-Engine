package com.nekgambling.application.usecase.player.bonus

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.vo.Currency
import com.nekgambling.application.event.player.bonus.BonusWageredEvent
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.repository.player.IPlayerBonusRepository

class WageredBonusUseCase(
    private val playerBonusRepository: IPlayerBonusRepository,
    private val eventAdapter: IEventAdapter,
    private val currencyAdapter: ICurrencyAdapter
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val bonus = playerBonusRepository.findById(command.id)
            .orElseThrow { NoSuchElementException("Bonus not found") }

        val updated = bonus.copy(
            status = PlayerBonus.Status.WAGERED,
            payoutAmount = currencyAdapter.convertUnitsToSystemUnits(command.payoutAmount, command.currency),
        )

        playerBonusRepository.save(updated)

        eventAdapter.publish(BonusWageredEvent(updated))
    }

    data class Command(
        val id: String,

        val payoutAmount: Long,

        val currency: Currency,
    )
}

package com.nekgambling.player.usecase.bonus

import com.nekgambling.core.adapter.ICurrencyAdapter
import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.vo.Currency
import com.nekgambling.player.event.bonus.BonusWageredEvent
import com.nekgambling.player.model.PlayerBonus
import com.nekgambling.player.repository.IPlayerBonusRepository

class WageredBonusUsecase(
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

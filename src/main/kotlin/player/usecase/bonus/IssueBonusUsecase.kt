package com.nekgambling.player.usecase.bonus

import com.nekgambling.core.adapter.ICurrencyAdapter
import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.vo.Currency
import com.nekgambling.player.event.bonus.BonusIssuedEvent
import com.nekgambling.player.model.PlayerBonus
import com.nekgambling.player.repository.IPlayerBonusRepository

class IssueBonusUsecase(
    private val playerBonusRepository: IPlayerBonusRepository,
    private val eventAdapter: IEventAdapter,
    private val currencyAdapter: ICurrencyAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val bonus = PlayerBonus(
            id = command.id,

            identity = command.identity,

            playerId = command.playerId,

            status = PlayerBonus.Status.ISSUE,

            amount = currencyAdapter.convertUnitsToSystemUnits(command.amount, command.currency),

            payoutAmount = 0L,
        )

        playerBonusRepository.save(bonus)

        eventAdapter.publish(BonusIssuedEvent(bonus))
    }

    data class Command(
        val id: String,

        val identity: String,

        val playerId: String,

        val amount: Long,

        val currency: Currency,
    )
}

package com.nekgambling.application.usecase.player.bonus

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.vo.Currency
import com.nekgambling.application.event.player.bonus.BonusIssuedEvent
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.player.repository.IPlayerBonusRepository

class IssueBonusUseCase(
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

        eventAdapter.publish(_root_ide_package_.com.nekgambling.application.event.player.bonus.BonusIssuedEvent(bonus))
    }

    data class Command(
        val id: String,

        val identity: String,

        val playerId: String,

        val amount: Long,

        val currency: Currency,
    )
}

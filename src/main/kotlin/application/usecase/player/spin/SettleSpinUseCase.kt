package com.nekgambling.application.usecase.player.spin

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.spin.SpinClosedEvent
import com.nekgambling.domain.repository.player.IPlayerSpinRepository
import kotlin.jvm.optionals.getOrElse

class SettleSpinUseCase(
    private val playerSpinRepository: IPlayerSpinRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val eventAdapter: IEventAdapter,
) {
    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val spin = playerSpinRepository.findBy(playerId = command.playerId, spinId = command.id, game = command.game)
            .getOrElse { error("Spin not found!, should be placed before settle") }
            .let {
                it.copy(
                    settleRealAmount = currencyAdapter.convertUnitsToSystemUnits(command.realAmount, it.spinCurrency),
                    settleBonusAmount = currencyAdapter.convertUnitsToSystemUnits(command.bonusAmount, it.spinCurrency)
                )
            }

        playerSpinRepository.save(spin)

        eventAdapter.publish(SpinClosedEvent(spin))
    }

    data class Command(
        val id: String,

        val playerId: String,

        val game: String,

        val realAmount: Long,
        val bonusAmount: Long,
    )
}
package com.nekgambling.player.usecase.spin

import com.nekgambling.core.adapter.ICurrencyAdapter
import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.vo.Currency
import com.nekgambling.player.event.spin.SpinClosedEvent
import com.nekgambling.player.event.spin.SpinOpenedEvent
import com.nekgambling.player.model.PlayerSpin
import com.nekgambling.player.repository.IPlayerSpinRepository
import kotlin.jvm.optionals.getOrElse
import kotlin.time.Clock

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
package com.nekgambling.application.usecase.player.spin

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.vo.Currency
import com.nekgambling.application.event.player.spin.SpinOpenedEvent
import com.nekgambling.domain.player.model.PlayerSpin
import com.nekgambling.domain.player.repository.IPlayerSpinRepository
import kotlinx.datetime.Clock

class PlaceSpinUseCase(
    private val playerSpinRepository: IPlayerSpinRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val eventAdapter: IEventAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val spin = PlayerSpin(
            id = command.id,

            playerId = command.playerId,

            freespinId = command.freespinId,

            spinCurrency = command.currency,

            game = command.game,

            placeRealAmount = currencyAdapter.convertUnitsToSystemUnits(command.realAmount, command.currency),
            placeBonusAmount = currencyAdapter.convertUnitsToSystemUnits(command.bonusAmount, command.currency),

            settleRealAmount = 0L,
            settleBonusAmount = 0L,

            createdAt = Clock.System.now(),
        )

        playerSpinRepository.save(spin)

        eventAdapter.publish(SpinOpenedEvent(spin))
    }

    data class Command(
        val id: String,

        val freespinId: String? = null,

        val playerId: String,

        val game: String,

        val currency: Currency,

        val realAmount: Long,
        val bonusAmount: Long,
    )
}
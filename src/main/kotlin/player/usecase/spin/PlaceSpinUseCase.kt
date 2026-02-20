package com.nekgambling.player.usecase.spin

import com.nekgambling.core.adapter.ICurrencyAdapter
import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.vo.Currency
import com.nekgambling.player.event.spin.SpinOpenedEvent
import com.nekgambling.player.model.PlayerSpin
import com.nekgambling.player.repository.IPlayerSpinRepository
import kotlin.time.Clock

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
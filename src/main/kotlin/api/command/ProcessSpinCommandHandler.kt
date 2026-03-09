package com.nekgambling.api.command

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.spin.SpinClosedEvent
import com.nekgambling.application.event.player.spin.SpinOpenedEvent
import com.nekgambling.domain.model.player.PlayerSpin
import com.nekgambling.domain.repository.player.IPlayerSpinRepository
import com.nekgambling.domain.vo.Currency
import kotlinx.datetime.Clock
import kotlin.jvm.optionals.getOrElse
import kotlin.reflect.KClass

class ProcessSpinCommandHandler(
    private val spinRepository: IPlayerSpinRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val eventAdapter: IEventAdapter,
) : ICommandHandler<ProcessSpinCommand, Unit> {

    override val commandType: KClass<ProcessSpinCommand> = ProcessSpinCommand::class

    override suspend fun handle(command: ProcessSpinCommand) {
        val currency = Currency(command.currency)
        val game = "${command.gameProvider}:${command.gameIdentity}"

        val exists = spinRepository.findBy(
            playerId = command.playerId,
            spinId = command.id,
            game = game,
        ).isPresent

        if (!exists) {
            val spin = PlayerSpin(
                id = command.id,
                playerId = command.playerId,
                spinCurrency = currency,
                game = game,
                placeRealAmount = currencyAdapter.convertUnitsToSystemUnits(
                    currencyAdapter.convertToUnits(command.placeRealAmount, currency), currency
                ),
                placeBonusAmount = currencyAdapter.convertUnitsToSystemUnits(
                    currencyAdapter.convertToUnits(command.placeBonusAmount, currency), currency
                ),
                settleRealAmount = 0L,
                settleBonusAmount = 0L,
                createdAt = Clock.System.now(),
            )

            spinRepository.save(spin)
            eventAdapter.publish(SpinOpenedEvent(spin))
        }

        val spin = spinRepository.findBy(
            playerId = command.playerId,
            spinId = command.id,
            game = game,
        ).getOrElse { error("Spin not found, should be placed before settle") }

        val settledSpin = spin.copy(
            settleRealAmount = currencyAdapter.convertUnitsToSystemUnits(
                currencyAdapter.convertToUnits(command.settleRealAmount, currency), spin.spinCurrency
            ),
            settleBonusAmount = currencyAdapter.convertUnitsToSystemUnits(
                currencyAdapter.convertToUnits(command.settleBonusAmount, currency), spin.spinCurrency
            ),
        )

        spinRepository.save(settledSpin)
        eventAdapter.publish(SpinClosedEvent(settledSpin))
    }
}

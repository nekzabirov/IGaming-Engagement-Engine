package com.nekgambling.api.command

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.usecase.player.spin.PlaceSpinUseCase
import com.nekgambling.application.usecase.player.spin.SettleSpinUseCase
import com.nekgambling.domain.player.repository.IPlayerSpinRepository
import com.nekgambling.domain.vo.Currency
import kotlin.reflect.KClass

class ProcessSpinCommandHandler(
    private val spinRepository: IPlayerSpinRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val placeSpinUseCase: PlaceSpinUseCase,
    private val settleSpinUseCase: SettleSpinUseCase,
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
            placeSpinUseCase(
                PlaceSpinUseCase.Command(
                    id = command.id,
                    playerId = command.playerId,
                    game = game,
                    currency = currency,
                    realAmount = currencyAdapter.convertToUnits(command.placeRealAmount, currency),
                    bonusAmount = currencyAdapter.convertToUnits(command.placeBonusAmount, currency),
                )
            ).getOrThrow()
        }

        settleSpinUseCase(
            SettleSpinUseCase.Command(
                id = command.id,
                playerId = command.playerId,
                game = game,
                realAmount = currencyAdapter.convertToUnits(command.settleRealAmount, currency),
                bonusAmount = currencyAdapter.convertToUnits(command.settleBonusAmount, currency),
            )
        ).getOrThrow()
    }
}

package com.nekgambling.application.usecase.player.freespin

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.vo.Currency
import com.nekgambling.application.event.player.freespin.FreespinPlayedEvent
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.repository.player.IPlayerFreespinRepository

class FinishFreespinUseCase(
    private val playerFreespinRepository: IPlayerFreespinRepository,
    private val eventAdapter: IEventAdapter,
    private val currencyAdapter: ICurrencyAdapter,
) {

    suspend operator fun invoke(command: Command): Result<Unit> = runCatching {
        val freespin = playerFreespinRepository.findBy(command.playerId, command.freespinId)
            .orElseThrow { NoSuchElementException("Freespin not found") }

        val updated = freespin.copy(
            status = PlayerFreespin.Status.PLAYED,
            payoutRealAmount = currencyAdapter.convertUnitsToSystemUnits(command.payoutRealAmount, command.currency),
        )

        playerFreespinRepository.save(updated)

        eventAdapter.publish(FreespinPlayedEvent(updated))
    }

    data class Command(
        val playerId: String,

        val freespinId: String,

        val payoutRealAmount: Long,

        val currency: Currency
    )
}

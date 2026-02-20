package com.nekgambling.application.usecase.player.invoice

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.vo.Currency
import com.nekgambling.application.event.player.invoice.InvoiceCreatedEvent
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.player.repository.IPlayerInvoiceRepository
import kotlinx.datetime.Instant

class CreateInvoiceUseCase(
    private val invoiceRepository: IPlayerInvoiceRepository,
    private val eventAdapter: IEventAdapter,
    private val currencyAdapter: ICurrencyAdapter,
)  {

    suspend operator fun invoke(playerId: String, transaction: Transaction): Result<Unit> = runCatching {
        val invoice = PlayerInvoice(
            id = transaction.id,

            playerId = playerId,

            type = transaction.type,

            status = PlayerInvoice.Status.IN_PROGRESS,

            transactionCurrency = transaction.currency,

            amount = currencyAdapter.convertUnitsToSystemUnits(transaction.amount, transaction.currency),

            transactionAmount = 0L,
            taxAmount = 0L,
            feeAmount = 0L,

            createdAt = transaction.createdAt,
        )

        invoiceRepository.save(invoice)

        eventAdapter.publish(InvoiceCreatedEvent(invoice))
    }

    data class Transaction(
        val id: String,

        val type: PlayerInvoice.Type,

        val currency: Currency,

        val amount: Long,

        val createdAt: Instant,
    )
}
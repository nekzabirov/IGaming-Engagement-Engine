package com.nekgambling.application.usecase.player.invoice

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.invoice.InvoiceUpdatedEvent
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.player.repository.IPlayerInvoiceRepository
import kotlin.jvm.optionals.getOrElse

class UpdateInvoiceUseCase(
    private val invoiceRepository: IPlayerInvoiceRepository,
    private val eventAdapter: IEventAdapter,
    private val currencyAdapter: ICurrencyAdapter,
) {

    suspend operator fun invoke(invoiceId: String, details: Details): Result<Unit> = runCatching {
        val invoice = invoiceRepository.findById(invoiceId).getOrElse {
            error("Invoice with id $invoiceId does not exist")
        }

        invoice.apply {
            updateStatus(details.status)
            updateTransactionAmount(currencyAdapter.convertUnitsToSystemUnits(details.transactionAmount, transactionCurrency))
            updateTaxAmount(currencyAdapter.convertUnitsToSystemUnits(details.taxAmount, transactionCurrency))
            updateFeeAmount(currencyAdapter.convertUnitsToSystemUnits(details.feeAmount, transactionCurrency))
        }

        invoiceRepository.save(invoice)

        eventAdapter.publish(InvoiceUpdatedEvent(invoice))
    }

    data class Details(
        val status: PlayerInvoice.Status,

        val transactionAmount: Long,
        val taxAmount: Long,
        val feeAmount: Long,
    )
}
package com.nekgambling.api.command

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.cqrs.command.ICommandHandler
import com.nekgambling.application.event.player.invoice.InvoiceCreatedEvent
import com.nekgambling.application.event.player.invoice.InvoiceUpdatedEvent
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.repository.player.IPlayerInvoiceRepository
import com.nekgambling.domain.vo.Currency
import kotlinx.datetime.Instant
import kotlin.jvm.optionals.getOrElse
import kotlin.reflect.KClass

class ProcessInvoiceCommandHandler(
    private val invoiceRepository: IPlayerInvoiceRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val eventAdapter: IEventAdapter,
) : ICommandHandler<ProcessInvoiceCommand> {

    override val commandType: KClass<ProcessInvoiceCommand> = ProcessInvoiceCommand::class

    override suspend fun handle(command: ProcessInvoiceCommand) {
        val currency = Currency(command.currency)
        val amount = currencyAdapter.convertToUnits(command.amount, currency)

        val exists = invoiceRepository.findById(command.transactionId).isPresent

        if (!exists) {
            val invoice = PlayerInvoice(
                id = command.transactionId,
                playerId = command.playerId,
                type = command.type.toDomain(),
                status = PlayerInvoice.Status.IN_PROGRESS,
                transactionCurrency = currency,
                amount = currencyAdapter.convertUnitsToSystemUnits(amount, currency),
                transactionAmount = 0L,
                taxAmount = 0L,
                feeAmount = 0L,
                createdAt = Instant.fromEpochMilliseconds(command.date),
            )

            invoiceRepository.save(invoice)
            eventAdapter.publish(InvoiceCreatedEvent(invoice))
        }

        val invoice = invoiceRepository.findById(command.transactionId).getOrElse {
            error("Invoice with id ${command.transactionId} does not exist")
        }

        invoice.apply {
            updateStatus(command.status.toDomain())
            updateTransactionAmount(currencyAdapter.convertUnitsToSystemUnits(
                currencyAdapter.convertToUnits(command.transactionAmount, currency), transactionCurrency
            ))
            updateTaxAmount(currencyAdapter.convertUnitsToSystemUnits(
                currencyAdapter.convertToUnits(command.taxAmount, currency), transactionCurrency
            ))
            updateFeeAmount(currencyAdapter.convertUnitsToSystemUnits(
                currencyAdapter.convertToUnits(command.feeAmount, currency), transactionCurrency
            ))
        }

        invoiceRepository.save(invoice)
        eventAdapter.publish(InvoiceUpdatedEvent(invoice))
    }

    private fun ProcessInvoiceCommand.Type.toDomain(): PlayerInvoice.Type = when (this) {
        ProcessInvoiceCommand.Type.DEPOSIT -> PlayerInvoice.Type.DEPOSIT
        ProcessInvoiceCommand.Type.WITHDRAW -> PlayerInvoice.Type.PAYOUT
    }

    private fun ProcessInvoiceCommand.Status.toDomain(): PlayerInvoice.Status = when (this) {
        ProcessInvoiceCommand.Status.SUCCESS -> PlayerInvoice.Status.SUCCESS
        ProcessInvoiceCommand.Status.REJECTED -> PlayerInvoice.Status.REJECTED
        ProcessInvoiceCommand.Status.IN_PROGRESS -> PlayerInvoice.Status.IN_PROGRESS
    }
}

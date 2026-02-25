package com.nekgambling.api.command

import com.nekgambling.application.adapter.ICurrencyAdapter

import com.nekgambling.application.usecase.player.invoice.CreateInvoiceUseCase
import com.nekgambling.application.usecase.player.invoice.UpdateInvoiceUseCase
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.player.repository.IPlayerInvoiceRepository
import com.nekgambling.domain.vo.Currency
import kotlinx.datetime.Instant
import kotlin.reflect.KClass

class ProcessInvoiceCommandHandler(
    private val invoiceRepository: IPlayerInvoiceRepository,
    private val currencyAdapter: ICurrencyAdapter,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val updateInvoiceUseCase: UpdateInvoiceUseCase,
) : ICommandHandler<ProcessInvoiceCommand, Unit> {

    override val commandType: KClass<ProcessInvoiceCommand> = ProcessInvoiceCommand::class

    override suspend fun handle(command: ProcessInvoiceCommand) {
        val currency = Currency(command.currency)
        val amount = currencyAdapter.convertToUnits(command.amount, currency)

        val exists = invoiceRepository.findById(command.transactionId).isPresent

        if (!exists) {
            createInvoiceUseCase(
                playerId = command.playerId,
                transaction = CreateInvoiceUseCase.Transaction(
                    id = command.transactionId,
                    type = command.type.toDomain(),
                    currency = currency,
                    amount = amount,
                    createdAt = Instant.fromEpochMilliseconds(command.date),
                )
            ).getOrThrow()
        }

        updateInvoiceUseCase(
            invoiceId = command.transactionId,
            details = UpdateInvoiceUseCase.Details(
                status = command.status.toDomain(),
                transactionAmount = currencyAdapter.convertToUnits(command.transactionAmount, currency),
                taxAmount = currencyAdapter.convertToUnits(command.taxAmount, currency),
                feeAmount = currencyAdapter.convertToUnits(command.feeAmount, currency),
            )
        ).getOrThrow()
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

package com.nekgambling.api

import com.nekgambling.api.command.ProcessInvoiceCommand
import com.nekgambling.api.command.ProcessPlayerCommand
import com.nekgambling.api.command.ProcessSpinCommand
import com.nekgambling.api.dto.*
import com.nekgambling.api.command.CommandBus
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.getKoin

fun Routing.eventApi() = route("event") {
    post() {
        val event = call.receive<EventRequest>()

        val commandBus = application.getKoin().get<CommandBus>()

        when (val payload = event.payload) {
            is UserPayload -> commandBus.execute(
                ProcessPlayerCommand(
                    playerId = event.userExtId,
                    gender = payload.gender,
                    externalAccountStatus = payload.externalAccountStatus,
                    email = payload.email,
                    phone = payload.phone,
                    username = payload.username,
                    emailConfirmed = payload.emailConfirmed,
                    kycStatus = payload.kycStatus,
                    phoneConfirmed = payload.phoneConfirmed,
                    registrationDate = payload.registrationDate,
                    userLanguage = payload.userLanguage,
                    userBirthdate = payload.userBirthdate,
                    userCountry = payload.userCountry,
                    firstName = payload.firstName,
                    lastName = payload.lastName,
                )
            )

            is DepositApprovePayload -> commandBus.execute(
                ProcessInvoiceCommand(
                    playerId = event.userExtId,
                    transactionId = payload.transactionId,
                    amount = payload.amount,
                    date = payload.date,
                    paymentMethod = payload.paymentMethod,
                    currency = payload.currency,
                    type = ProcessInvoiceCommand.Type.DEPOSIT,
                    status = event.type.toDepositStatus(),
                )
            )

            is WithdrawPayload -> commandBus.execute(
                ProcessInvoiceCommand(
                    playerId = event.userExtId,
                    transactionId = payload.transactionId,
                    amount = payload.amount,
                    date = payload.date,
                    paymentMethod = payload.paymentMethod,
                    currency = payload.currency,
                    type = ProcessInvoiceCommand.Type.WITHDRAW,
                    status = event.type.toWithdrawStatus(),
                )
            )

            is CasinoBetWinPayload -> commandBus.execute(
                ProcessSpinCommand(
                    playerId = event.userExtId,
                    id = payload.betId,
                    placeRealAmount = payload.betAmountReal,
                    placeBonusAmount = payload.betAmountBonus,
                    settleRealAmount = payload.winAmountReal,
                    settleBonusAmount = payload.winAmountBonus,
                    gameProvider = payload.gameProvider,
                    gameIdentity = payload.gameName,
                    currency = payload.currency,
                )
            )
        }

        call.respond(HttpStatusCode.Created)
    }
}

private fun String.toDepositStatus(): ProcessInvoiceCommand.Status = when (this) {
    "acc_deposit_approved" -> ProcessInvoiceCommand.Status.SUCCESS
    "acc_deposit_failed" -> ProcessInvoiceCommand.Status.REJECTED
    else -> ProcessInvoiceCommand.Status.IN_PROGRESS
}

private fun String.toWithdrawStatus(): ProcessInvoiceCommand.Status = when (this) {
    "acc_withdrawal_completed" -> ProcessInvoiceCommand.Status.SUCCESS
    "acc_withdrawal_cancelled" -> ProcessInvoiceCommand.Status.REJECTED
    else -> ProcessInvoiceCommand.Status.IN_PROGRESS
}
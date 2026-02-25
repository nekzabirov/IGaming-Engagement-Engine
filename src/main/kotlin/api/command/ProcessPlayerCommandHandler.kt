package com.nekgambling.api.command

import com.nekgambling.api.dto.AccountStatus
import com.nekgambling.api.dto.UserGender

import com.nekgambling.application.usecase.player.player.UpdatePlayerUseCase
import com.nekgambling.domain.player.model.PlayerDetails
import com.nekgambling.domain.vo.Country
import com.nekgambling.domain.vo.Locale
import kotlinx.datetime.Instant
import java.util.Date
import kotlin.reflect.KClass

class ProcessPlayerCommandHandler(
    private val updatePlayerUseCase: UpdatePlayerUseCase,
) : ICommandHandler<ProcessPlayerCommand, Unit> {

    override val commandType: KClass<ProcessPlayerCommand> = ProcessPlayerCommand::class

    override suspend fun handle(command: ProcessPlayerCommand) {
        val details = UpdatePlayerUseCase.Details(
            username = command.username,
            email = command.email,
            phone = command.phone,
            emailConfirmed = command.emailConfirmed,
            phoneConfirmed = command.phoneConfirmed,
            status = command.externalAccountStatus?.let {
                when (it) {
                    AccountStatus.ACTIVE -> PlayerDetails.Status.ACTIVE
                    AccountStatus.BLOCKED -> PlayerDetails.Status.BLOCKED
                    AccountStatus.SUSPENDED -> PlayerDetails.Status.FROZEN
                    AccountStatus.SELF_EXCLUDED -> PlayerDetails.Status.FROZEN
                    else -> PlayerDetails.Status.ACTIVE
                }
            },
            firstName = command.firstName,
            lastName = command.lastName,
            middleName = null,
            birthDate = command.userBirthdate?.let { Date(it) },
            country = command.userCountry?.let { Country(it) },
            locale = command.userLanguage?.let { Locale(it) },
            personalNumber = null,
            isVerified = command.kycStatus?.let { it == "VERIFIED" },
            gender = command.gender?.let {
                when (it) {
                    UserGender.MALE -> PlayerDetails.Gender.MALE
                    UserGender.FEMALE -> PlayerDetails.Gender.FEMALE
                    else -> PlayerDetails.Gender.OTHER
                }
            },
            address = null,
            affiliateTag = null,
            registeredAt = command.registrationDate?.let { Instant.fromEpochMilliseconds(it) },
        )

        updatePlayerUseCase(command.playerId, details)
    }
}

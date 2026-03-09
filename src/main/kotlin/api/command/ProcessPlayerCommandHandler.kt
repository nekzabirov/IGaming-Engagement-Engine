package com.nekgambling.api.command

import com.nekgambling.api.dto.AccountStatus
import com.nekgambling.api.dto.UserGender
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.player.PlayerRegisteredEvent
import com.nekgambling.application.event.player.player.PlayerUpdatedEvent
import com.nekgambling.domain.model.player.PlayerDetails
import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.domain.vo.Country
import com.nekgambling.domain.vo.Locale
import kotlinx.datetime.Instant
import java.util.Date
import kotlin.jvm.optionals.getOrElse
import kotlin.reflect.KClass

class ProcessPlayerCommandHandler(
    private val playerRepository: IPlayerDetailsRepository,
    private val eventAdapter: IEventAdapter,
) : ICommandHandler<ProcessPlayerCommand, Unit> {

    override val commandType: KClass<ProcessPlayerCommand> = ProcessPlayerCommand::class

    override suspend fun handle(command: ProcessPlayerCommand) {
        val status = command.externalAccountStatus?.let {
            when (it) {
                AccountStatus.ACTIVE -> PlayerDetails.Status.ACTIVE
                AccountStatus.BLOCKED -> PlayerDetails.Status.BLOCKED
                AccountStatus.SUSPENDED -> PlayerDetails.Status.FROZEN
                AccountStatus.SELF_EXCLUDED -> PlayerDetails.Status.FROZEN
                else -> PlayerDetails.Status.ACTIVE
            }
        }
        val gender = command.gender?.let {
            when (it) {
                UserGender.MALE -> PlayerDetails.Gender.MALE
                UserGender.FEMALE -> PlayerDetails.Gender.FEMALE
                else -> PlayerDetails.Gender.OTHER
            }
        }

        var isNewPlayer = false
        val player: PlayerDetails = playerRepository.findById(command.playerId)
            .getOrElse {
                isNewPlayer = true
                PlayerDetails(
                    id = command.playerId,
                    username = command.username,
                    email = command.email,
                    phone = command.phone,
                    emailConfirmed = command.emailConfirmed ?: false,
                    phoneConfirmed = command.phoneConfirmed ?: false,
                    status = PlayerDetails.Status.ACTIVE,
                    firstName = command.firstName,
                    lastName = command.lastName,
                    middleName = null,
                    birthDate = command.userBirthdate?.let { Date(it) },
                    country = command.userCountry?.let { Country(it) },
                    locale = command.userLanguage?.let { Locale(it) },
                    personalNumber = null,
                    isVerified = command.kycStatus?.let { it == "VERIFIED" } ?: false,
                    gender = gender,
                    address = null,
                    affiliateTag = null,
                    registeredAt = command.registrationDate?.let { Instant.fromEpochMilliseconds(it) }
                        ?: error("registeredAt is required"),
                )
            }

        command.username?.let { player.updateUsername(it) }
        command.email?.let { player.updateEmail(it) }
        command.phone?.let { player.updatePhone(it) }
        command.emailConfirmed?.let { player.updateEmailConfirmed(it) }
        command.phoneConfirmed?.let { player.updatePhoneConfirmed(it) }
        status?.let { player.updateStatus(it) }
        command.firstName?.let { player.updateFirstName(it) }
        command.lastName?.let { player.updateLastName(it) }
        command.userBirthdate?.let { player.updateBirthDate(Date(it)) }
        command.userCountry?.let { player.updateCountry(Country(it)) }
        command.userLanguage?.let { player.updateLocale(Locale(it)) }
        command.kycStatus?.let { player.updateIsVerified(it == "VERIFIED") }
        gender?.let { player.updateGender(it) }

        playerRepository.save(player)

        if (isNewPlayer)
            eventAdapter.publish(PlayerRegisteredEvent(playerId = command.playerId, details = player))
        else
            eventAdapter.publish(PlayerUpdatedEvent(playerId = command.playerId, details = player))
    }
}

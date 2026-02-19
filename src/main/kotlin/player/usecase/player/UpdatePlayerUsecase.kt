package com.nekgambling.player.usecase.player

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.core.vo.Country
import com.nekgambling.core.vo.Locale
import com.nekgambling.player.event.player.PlayerRegisteredEvent
import com.nekgambling.player.event.player.PlayerUpdatedEvent
import com.nekgambling.player.model.PlayerDetails
import com.nekgambling.player.repository.IPlayerDetailsRepository
import java.util.Date
import kotlin.jvm.optionals.getOrElse
import kotlin.time.Instant

class UpdatePlayerUsecase(
    private val playerRepository: IPlayerDetailsRepository,
    private val eventAdapter: IEventAdapter,
) {
    suspend operator fun invoke(playerId: String, details: Details): Result<Unit> = runCatching {
        var isNewPlayer = false
        val player: PlayerDetails = playerRepository.findById(playerId)
            .getOrElse {
            isNewPlayer = true
                PlayerDetails(
                    id = playerId,
                    username = details.username,
                    email = details.email,
                    phone = details.phone,
                    emailConfirmed = details.emailConfirmed ?: false,
                    phoneConfirmed = details.phoneConfirmed ?: false,
                    status = PlayerDetails.Status.ACTIVE,
                    firstName = details.firstName,
                    lastName = details.lastName,
                    middleName = details.middleName,
                    birthDate = details.birthDate,
                    country = details.country,
                    locale = details.locale,
                    personalNumber = details.personalNumber,
                    isVerified = details.isVerified ?: false,
                    gender = details.gender,
                    address = details.address,
                    affiliateTag = details.affiliateTag,
                    registeredAt = requireNotNull(details.registeredAt) { "registeredAt is required" },
                )
        }

        details.username?.let { player.updateUsername(it) }
        details.email?.let { player.updateEmail(it) }
        details.phone?.let { player.updatePhone(it) }
        details.emailConfirmed?.let { player.updateEmailConfirmed(it) }
        details.phoneConfirmed?.let { player.updatePhoneConfirmed(it) }
        details.status?.let { player.updateStatus(it) }
        details.firstName?.let { player.updateFirstName(it) }
        details.lastName?.let { player.updateLastName(it) }
        details.middleName?.let { player.updateMiddleName(it) }
        details.birthDate?.let { player.updateBirthDate(it) }
        details.country?.let { player.updateCountry(it) }
        details.locale?.let { player.updateLocale(it) }
        details.personalNumber?.let { player.updatePersonalNumber(it) }
        details.isVerified?.let { player.updateIsVerified(it) }
        details.gender?.let { player.updateGender(it) }
        details.address?.let { player.updateAddress(it) }
        details.affiliateTag?.let { player.updateAffiliateTag(it) }

        playerRepository.save(player)

        if (isNewPlayer)
            eventAdapter.publish(PlayerRegisteredEvent(playerId = playerId, details = player))
        else
            eventAdapter.publish(PlayerUpdatedEvent(playerId = playerId, details = player))
    }

    data class Details(
        val username: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val emailConfirmed: Boolean? = null,
        val phoneConfirmed: Boolean? = null,
        val status: PlayerDetails.Status? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val middleName: String? = null,
        val birthDate: Date? = null,
        val country: Country? = null,
        val locale: Locale? = null,
        val personalNumber: String? = null,
        val isVerified: Boolean? = null,
        val gender: PlayerDetails.Gender? = null,
        val address: String? = null,
        val affiliateTag: String? = null,
        val registeredAt: Instant? = null,
    )
}
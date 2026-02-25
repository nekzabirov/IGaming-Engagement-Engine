package com.nekgambling.api

import com.nekgambling.api.dto.AccountStatus
import com.nekgambling.api.dto.EventPayload
import com.nekgambling.api.dto.EventRequest
import com.nekgambling.api.dto.UserGender
import com.nekgambling.api.dto.UserPayload
import com.nekgambling.application.usecase.player.player.UpdatePlayerUseCase
import com.nekgambling.domain.player.model.PlayerDetails
import com.nekgambling.domain.vo.Country
import com.nekgambling.domain.vo.Locale
import com.nekgambling.user.domain.model.Profile
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.Instant
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.koin
import java.util.Date

fun Routing.eventApi() = route("event") {
    post() {
        val event = call.receive<EventRequest>()

        when (event.payload) {
            is UserPayload -> application.processProfileEvent(event.userExtId, event.payload)
        }

        call.respond(HttpStatusCode.Created)
    }
}

private suspend fun Application.processProfileEvent(userId: String, payload: UserPayload) {
    val updatePlayerUseCase = getKoin().get<UpdatePlayerUseCase>()

    val details = UpdatePlayerUseCase.Details(
        username = payload.username,
        email = payload.email,
        phone = payload.phone,
        emailConfirmed = payload.emailConfirmed,
        phoneConfirmed = payload.phoneConfirmed,
        status = payload.externalAccountStatus?.let {
            when (it) {
                AccountStatus.ACTIVE -> PlayerDetails.Status.ACTIVE
                AccountStatus.BLOCKED -> PlayerDetails.Status.BLOCKED
                AccountStatus.SUSPENDED -> PlayerDetails.Status.FROZEN
                AccountStatus.SELF_EXCLUDED -> PlayerDetails.Status.FROZEN
                else -> PlayerDetails.Status.ACTIVE
            }
        },
        firstName = payload.firstName,
        lastName = payload.lastName,
        middleName = null,
        birthDate = payload.userBirthdate?.let { Date(it) },
        country = payload.userCountry?.let { Country(it) },
        locale = payload.userLanguage?.let { Locale(it) },
        personalNumber = null,
        isVerified = payload.kycStatus?.let { it == "VERIFIED" },
        gender = payload.gender?.let {
            when (it) {
                UserGender.MALE -> PlayerDetails.Gender.MALE
                UserGender.FEMALE -> PlayerDetails.Gender.FEMALE
                else -> PlayerDetails.Gender.OTHER
            }
        },
        address = null,
        affiliateTag = null,
        registeredAt = payload.registrationDate?.let { Instant.fromEpochMilliseconds(it) }
    )

    updatePlayerUseCase(userId, details)
}
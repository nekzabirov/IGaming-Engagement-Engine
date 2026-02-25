package com.nekgambling.api.command

import com.nekgambling.api.dto.AccountStatus
import com.nekgambling.api.dto.UserGender


data class ProcessPlayerCommand(
    val playerId: String,
    val gender: UserGender? = null,
    val externalAccountStatus: AccountStatus? = null,
    val email: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val emailConfirmed: Boolean? = null,
    val kycStatus: String? = null,
    val phoneConfirmed: Boolean? = null,
    val registrationDate: Long? = null,
    val userLanguage: String? = null,
    val userBirthdate: Long? = null,
    val userCountry: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
) : ICommand<Unit>

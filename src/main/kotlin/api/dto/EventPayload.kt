package com.nekgambling.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface EventPayload

@Serializable
data class UserPayload(
    @SerialName("core_user_gender")
    val gender: UserGender? = null,

    @SerialName("core_external_account_status")
    val externalAccountStatus: AccountStatus? = null,

    @SerialName("user_email")
    val email: String? = null,

    @SerialName("user_phone")
    val phone: String? = null,

    @SerialName("core_username")
    val username: String? = null,

    @SerialName("core_email_confirmed")
    val emailConfirmed: Boolean? = null,

    @SerialName("core_kyc_status")
    val kycStatus: String? = null,

    @SerialName("core_phone_confirmed")
    val phoneConfirmed: Boolean? = null,

    @SerialName("core_registration_date")
    //timestamp Number
    val registrationDate: Long? = null,

    @SerialName("core_user_language")
    val userLanguage: String? = null,

    @SerialName("user_birthdate")
    //timestamp Number
    val userBirthdate: Long? = null,

    @SerialName("user_country")
    val userCountry: String? = null,

    @SerialName("user_first_name")
    val firstName: String? = null,

    @SerialName("user_last_name")
    val lastName: String? = null
) : EventPayload

@Serializable
enum class AccountStatus {
    ACTIVE, BLOCKED, SUSPENDED, BANNED, SELF_EXCLUDED,
    DEACTIVATED, PENDING, APPROVED
}

@Serializable
enum class UserGender {
    MALE, FEMALE, F, M, UNKNOWN
}
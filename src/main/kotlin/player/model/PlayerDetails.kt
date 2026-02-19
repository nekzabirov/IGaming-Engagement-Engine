package com.nekgambling.player.model

import com.nekgambling.core.vo.Country
import com.nekgambling.core.vo.Locale
import kotlinx.datetime.Instant
import java.util.Date

data class PlayerDetails(
    val id: String,

    var username: String? = null,

    var email: String? = null,
    var phone: String? = null,
    var emailConfirmed: Boolean,
    var phoneConfirmed: Boolean,

    var status: Status,

    var firstName: String? = null,
    var lastName: String? = null,
    var middleName: String? = null,

    var birthDate: Date? = null,

    var country: Country? = null,

    var locale: Locale? = null,

    var personalNumber: String? = null,

    var isVerified: Boolean,

    var gender: Gender? = null,

    var address: String? = null,

    var affiliateTag: String? = null,

    val registeredAt: Instant,
) {
    enum class Status {
        ACTIVE,
        BLOCKED,
        FROZEN
    }

    enum class Gender {
        MALE,
        FEMALE,
        OTHER
    }

    fun updateUsername(username: String) {
        this.username = username
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updatePhone(phone: String) {
        this.phone = phone
    }

    fun updateEmailConfirmed(emailConfirmed: Boolean) {
        this.emailConfirmed = emailConfirmed
    }

    fun updatePhoneConfirmed(phoneConfirmed: Boolean) {
        this.phoneConfirmed = phoneConfirmed
    }

    fun updateStatus(status: Status) {
        this.status = status
    }

    fun updateFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun updateLastName(lastName: String) {
        this.lastName = lastName
    }

    fun updateMiddleName(middleName: String) {
        this.middleName = middleName
    }

    fun updateBirthDate(birthDate: Date) {
        this.birthDate = birthDate
    }

    fun updateCountry(country: Country) {
        this.country = country
    }

    fun updateLocale(locale: Locale) {
        this.locale = locale
    }

    fun updatePersonalNumber(personalNumber: String) {
        this.personalNumber = personalNumber
    }

    fun updateIsVerified(isVerified: Boolean) {
        this.isVerified = isVerified
    }

    fun updateGender(gender: Gender) {
        this.gender = gender
    }

    fun updateAddress(address: String) {
        this.address = address
    }

    fun updateAffiliateTag(affiliateTag: String) {
        this.affiliateTag = affiliateTag
    }

}

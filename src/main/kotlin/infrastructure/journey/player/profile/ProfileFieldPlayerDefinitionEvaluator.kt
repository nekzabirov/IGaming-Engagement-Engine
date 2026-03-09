package com.nekgambling.infrastructure.journey.player.profile

import com.nekgambling.domain.model.player.PlayerDetails
import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlin.reflect.KClass

class ProfileFieldPlayerDefinitionEvaluator(private val playerDetailsRepository: IPlayerDetailsRepository) :
    IPlayerDefinitionEvaluator<ProfileFieldPlayerDefinition> {

    override val definition: KClass<ProfileFieldPlayerDefinition> = ProfileFieldPlayerDefinition::class

    override suspend fun evaluate(
        playerId: String,
        condition: ProfileFieldPlayerDefinition
    ): Boolean {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null) ?: return false

        return with(condition) {
            value == playerDetails.readValue(condition.field)
        }
    }

    private fun PlayerDetails.readValue(field: ProfileFieldPlayerDefinition.Field): Any? {
        return when (field) {
            ProfileFieldPlayerDefinition.Field.USERNAME -> username
            ProfileFieldPlayerDefinition.Field.EMAIL -> email
            ProfileFieldPlayerDefinition.Field.PHONE -> phone
            ProfileFieldPlayerDefinition.Field.EMAIL_CONFIRMED -> emailConfirmed
            ProfileFieldPlayerDefinition.Field.PHONE_CONFIRMED -> phoneConfirmed
            ProfileFieldPlayerDefinition.Field.STATUS -> status
            ProfileFieldPlayerDefinition.Field.FIRST_NAME -> firstName
            ProfileFieldPlayerDefinition.Field.LAST_NAME -> lastName
            ProfileFieldPlayerDefinition.Field.MIDDLE_NAME -> middleName
            ProfileFieldPlayerDefinition.Field.BIRTH_DATE -> birthDate
            ProfileFieldPlayerDefinition.Field.COUNTRY -> country
            ProfileFieldPlayerDefinition.Field.LOCALE -> locale
            ProfileFieldPlayerDefinition.Field.PERSONAL_NUMBER -> personalNumber
            ProfileFieldPlayerDefinition.Field.IS_VERIFIED -> isVerified
            ProfileFieldPlayerDefinition.Field.GENDER -> gender
            ProfileFieldPlayerDefinition.Field.ADDRESS -> address
            ProfileFieldPlayerDefinition.Field.AFFILIATE_TAG -> affiliateTag
            ProfileFieldPlayerDefinition.Field.REGISTERED_AT -> registeredAt
        }
    }

}
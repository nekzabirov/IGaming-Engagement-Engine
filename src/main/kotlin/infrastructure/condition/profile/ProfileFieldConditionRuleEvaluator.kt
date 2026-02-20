package com.nekgambling.infrastructure.condition.profile

import com.nekgambling.domain.player.model.PlayerDetails
import com.nekgambling.domain.player.repository.IPlayerDetailsRepository
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlin.reflect.KClass

class ProfileFieldConditionRuleEvaluator(private val playerDetailsRepository: IPlayerDetailsRepository) :
    IConditionRuleEvaluator<ProfileFieldConditionRule> {

    override val condition: KClass<ProfileFieldConditionRule> = ProfileFieldConditionRule::class

    override suspend fun evaluate(
        playerId: String,
        condition: ProfileFieldConditionRule
    ): Boolean {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null) ?: return false

        return with(condition) {
            value == playerDetails.readValue(condition.field)
        }
    }

    private fun PlayerDetails.readValue(field: ProfileFieldConditionRule.Field): Any? {
        return when (field) {
            ProfileFieldConditionRule.Field.USERNAME -> username
            ProfileFieldConditionRule.Field.EMAIL -> email
            ProfileFieldConditionRule.Field.PHONE -> phone
            ProfileFieldConditionRule.Field.EMAIL_CONFIRMED -> emailConfirmed
            ProfileFieldConditionRule.Field.PHONE_CONFIRMED -> phoneConfirmed
            ProfileFieldConditionRule.Field.STATUS -> status
            ProfileFieldConditionRule.Field.FIRST_NAME -> firstName
            ProfileFieldConditionRule.Field.LAST_NAME -> lastName
            ProfileFieldConditionRule.Field.MIDDLE_NAME -> middleName
            ProfileFieldConditionRule.Field.BIRTH_DATE -> birthDate
            ProfileFieldConditionRule.Field.COUNTRY -> country
            ProfileFieldConditionRule.Field.LOCALE -> locale
            ProfileFieldConditionRule.Field.PERSONAL_NUMBER -> personalNumber
            ProfileFieldConditionRule.Field.IS_VERIFIED -> isVerified
            ProfileFieldConditionRule.Field.GENDER -> gender
            ProfileFieldConditionRule.Field.ADDRESS -> address
            ProfileFieldConditionRule.Field.AFFILIATE_TAG -> affiliateTag
            ProfileFieldConditionRule.Field.REGISTERED_AT -> registeredAt
        }
    }

}
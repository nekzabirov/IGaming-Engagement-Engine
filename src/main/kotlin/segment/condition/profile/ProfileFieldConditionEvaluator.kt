package com.nekgambling.segment.condition.profile

import com.nekgambling.player.model.PlayerDetails
import com.nekgambling.player.query.FindPlayerDetailsQuery
import com.nekgambling.segment.condition.ICondition
import com.nekgambling.segment.condition.IConditionEvaluator
import kotlin.reflect.KClass

class ProfileFieldConditionEvaluator(private val findPlayerDetailsQuery: FindPlayerDetailsQuery) :
    IConditionEvaluator<ProfileFieldCondition> {

    override val condition: KClass<ProfileFieldCondition> = ProfileFieldCondition::class

    override suspend fun evaluate(
        playerId: String,
        condition: ProfileFieldCondition
    ): Boolean {
        val playerDetails = findPlayerDetailsQuery.execute(playerId).getOrElse { return false }

        return with(condition) {
            value == playerDetails.readValue(condition.field)
        }
    }

    private fun PlayerDetails.readValue(field: ProfileFieldCondition.Field): Any? {
        return when (field) {
            ProfileFieldCondition.Field.USERNAME -> username
            ProfileFieldCondition.Field.EMAIL -> email
            ProfileFieldCondition.Field.PHONE -> phone
            ProfileFieldCondition.Field.EMAIL_CONFIRMED -> emailConfirmed
            ProfileFieldCondition.Field.PHONE_CONFIRMED -> phoneConfirmed
            ProfileFieldCondition.Field.STATUS -> status
            ProfileFieldCondition.Field.FIRST_NAME -> firstName
            ProfileFieldCondition.Field.LAST_NAME -> lastName
            ProfileFieldCondition.Field.MIDDLE_NAME -> middleName
            ProfileFieldCondition.Field.BIRTH_DATE -> birthDate
            ProfileFieldCondition.Field.COUNTRY -> country
            ProfileFieldCondition.Field.LOCALE -> locale
            ProfileFieldCondition.Field.PERSONAL_NUMBER -> personalNumber
            ProfileFieldCondition.Field.IS_VERIFIED -> isVerified
            ProfileFieldCondition.Field.GENDER -> gender
            ProfileFieldCondition.Field.ADDRESS -> address
            ProfileFieldCondition.Field.AFFILIATE_TAG -> affiliateTag
            ProfileFieldCondition.Field.REGISTERED_AT -> registeredAt
        }
    }

}
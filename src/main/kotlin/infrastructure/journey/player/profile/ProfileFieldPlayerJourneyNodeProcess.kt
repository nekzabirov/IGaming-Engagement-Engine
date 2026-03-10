package com.nekgambling.infrastructure.journey.player.profile

import com.nekgambling.domain.model.player.PlayerDetails
import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.player.IPlayerJourneyNodeProcess
import kotlin.reflect.KClass

class ProfileFieldPlayerJourneyNodeProcess(
    private val playerDetailsRepository: IPlayerDetailsRepository,
) : IPlayerJourneyNodeProcess<ProfileFieldPlayerJourneyNode> {

    override val nodeType: KClass<ProfileFieldPlayerJourneyNode> = ProfileFieldPlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: ProfileFieldPlayerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null)

        val matched = if (playerDetails != null) {
            node.expectedValue == playerDetails.readValue(node.field)
        } else false

        return JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }

    private fun PlayerDetails.readValue(field: ProfileFieldPlayerJourneyNode.Field): Any? {
        return when (field) {
            ProfileFieldPlayerJourneyNode.Field.USERNAME -> username
            ProfileFieldPlayerJourneyNode.Field.EMAIL -> email
            ProfileFieldPlayerJourneyNode.Field.PHONE -> phone
            ProfileFieldPlayerJourneyNode.Field.EMAIL_CONFIRMED -> emailConfirmed
            ProfileFieldPlayerJourneyNode.Field.PHONE_CONFIRMED -> phoneConfirmed
            ProfileFieldPlayerJourneyNode.Field.STATUS -> status
            ProfileFieldPlayerJourneyNode.Field.FIRST_NAME -> firstName
            ProfileFieldPlayerJourneyNode.Field.LAST_NAME -> lastName
            ProfileFieldPlayerJourneyNode.Field.MIDDLE_NAME -> middleName
            ProfileFieldPlayerJourneyNode.Field.BIRTH_DATE -> birthDate
            ProfileFieldPlayerJourneyNode.Field.COUNTRY -> country
            ProfileFieldPlayerJourneyNode.Field.LOCALE -> locale
            ProfileFieldPlayerJourneyNode.Field.PERSONAL_NUMBER -> personalNumber
            ProfileFieldPlayerJourneyNode.Field.IS_VERIFIED -> isVerified
            ProfileFieldPlayerJourneyNode.Field.GENDER -> gender
            ProfileFieldPlayerJourneyNode.Field.ADDRESS -> address
            ProfileFieldPlayerJourneyNode.Field.AFFILIATE_TAG -> affiliateTag
            ProfileFieldPlayerJourneyNode.Field.REGISTERED_AT -> registeredAt
        }
    }
}

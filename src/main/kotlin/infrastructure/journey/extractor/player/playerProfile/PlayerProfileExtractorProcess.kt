package com.nekgambling.infrastructure.journey.extractor.player.playerProfile

import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.domain.vo.Payload
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode.Companion.buildOutput
import kotlin.reflect.KClass

class PlayerProfileExtractorProcess(
    private val playerDetailsRepository: IPlayerDetailsRepository,
) : ExtractorJourneyNodeProcess<PlayerProfileExtractor>() {

    override val nodeType: KClass<PlayerProfileExtractor> = PlayerProfileExtractor::class

    override suspend fun extract(
        playerId: String,
        node: PlayerProfileExtractor,
        payload: Payload,
    ): Payload {
        val player = playerDetailsRepository.findById(playerId)
            .orElse(null) ?: return emptyMap()

        return buildOutput(
            "username" to player.username,
            "email" to player.email,
            "phone" to player.phone,
            "emailConfirmed" to player.emailConfirmed,
            "phoneConfirmed" to player.phoneConfirmed,
            "status" to player.status.name,
            "firstName" to player.firstName,
            "lastName" to player.lastName,
            "middleName" to player.middleName,
            "birthDate" to player.birthDate?.toString(),
            "country" to player.country?.code,
            "locale" to player.locale?.value,
            "personalNumber" to player.personalNumber,
            "isVerified" to player.isVerified,
            "gender" to player.gender?.name,
            "address" to player.address,
            "affiliateTag" to player.affiliateTag,
            "registeredAt" to player.registeredAt.toString(),
        )
    }
}

package com.nekgambling.infrastructure.journey.extractor.playerProfile

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.infrastructure.journey.extractor.IExtractorJourneyNode

data class PlayerProfileExtractor(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    private val playerDetailsRepository: IPlayerDetailsRepository,
) : IExtractorJourneyNode(id, next) {

    companion object {
        const val PREFIX = "player"
    }

    override suspend fun extract(playerId: String, inputs: Map<String, Any>): Map<String, Any> {
        val player = playerDetailsRepository.findById(playerId)
            .orElse(null) ?: return emptyMap()

        val result = mutableMapOf<String, Any>()

        player.username?.let { result["$PREFIX:username"] = it }
        player.email?.let { result["$PREFIX:email"] = it }
        player.phone?.let { result["$PREFIX:phone"] = it }
        result["$PREFIX:emailConfirmed"] = player.emailConfirmed
        result["$PREFIX:phoneConfirmed"] = player.phoneConfirmed
        result["$PREFIX:status"] = player.status.name
        player.firstName?.let { result["$PREFIX:firstName"] = it }
        player.lastName?.let { result["$PREFIX:lastName"] = it }
        player.middleName?.let { result["$PREFIX:middleName"] = it }
        player.birthDate?.let { result["$PREFIX:birthDate"] = it.toString() }
        player.country?.let { result["$PREFIX:country"] = it.code }
        player.locale?.let { result["$PREFIX:locale"] = it.value }
        player.personalNumber?.let { result["$PREFIX:personalNumber"] = it }
        result["$PREFIX:isVerified"] = player.isVerified
        player.gender?.let { result["$PREFIX:gender"] = it.name }
        player.address?.let { result["$PREFIX:address"] = it }
        player.affiliateTag?.let { result["$PREFIX:affiliateTag"] = it }
        result["$PREFIX:registeredAt"] = player.registeredAt.toString()

        return result
    }
}

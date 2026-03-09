package com.nekgambling.infrastructure.journey.extractor.playerProfile

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object PlayerProfileExtractorParams : JourneyNodeNomenclature<PlayerProfileExtractor> {
    override val nodeType: KClass<PlayerProfileExtractor> = PlayerProfileExtractor::class

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${PlayerProfileExtractor.PREFIX}:username",
        "${PlayerProfileExtractor.PREFIX}:email",
        "${PlayerProfileExtractor.PREFIX}:phone",
        "${PlayerProfileExtractor.PREFIX}:emailConfirmed",
        "${PlayerProfileExtractor.PREFIX}:phoneConfirmed",
        "${PlayerProfileExtractor.PREFIX}:status",
        "${PlayerProfileExtractor.PREFIX}:firstName",
        "${PlayerProfileExtractor.PREFIX}:lastName",
        "${PlayerProfileExtractor.PREFIX}:middleName",
        "${PlayerProfileExtractor.PREFIX}:birthDate",
        "${PlayerProfileExtractor.PREFIX}:country",
        "${PlayerProfileExtractor.PREFIX}:locale",
        "${PlayerProfileExtractor.PREFIX}:personalNumber",
        "${PlayerProfileExtractor.PREFIX}:isVerified",
        "${PlayerProfileExtractor.PREFIX}:gender",
        "${PlayerProfileExtractor.PREFIX}:address",
        "${PlayerProfileExtractor.PREFIX}:affiliateTag",
        "${PlayerProfileExtractor.PREFIX}:registeredAt",
    )
}

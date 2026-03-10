package com.nekgambling.infrastructure.journey.extractor.player.playerAge

import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.infrastructure.journey.extractor.ExtractorJourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode.Companion.buildOutput
import kotlinx.datetime.*
import kotlin.reflect.KClass

class PlayerAgeExtractorProcess(
    private val playerDetailsRepository: IPlayerDetailsRepository,
) : ExtractorJourneyNodeProcess<PlayerAgeExtractor>() {

    override val nodeType: KClass<PlayerAgeExtractor> = PlayerAgeExtractor::class

    override suspend fun extract(
        playerId: String,
        node: PlayerAgeExtractor,
        payload: Map<String, Any>,
    ): Map<String, Any> {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null)
            ?: return emptyMap()

        val playerBirthDate = playerDetails.birthDate ?: return emptyMap()

        val birthLocalDate = playerBirthDate.toInstant().toKotlinInstant()
            .toLocalDateTime(TimeZone.UTC).date
        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val playerAge = birthLocalDate.until(today, DateTimeUnit.YEAR)

        return buildOutput("age" to playerAge)
    }
}

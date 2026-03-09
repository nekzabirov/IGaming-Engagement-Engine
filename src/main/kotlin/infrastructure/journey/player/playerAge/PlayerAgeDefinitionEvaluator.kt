package com.nekgambling.infrastructure.journey.player.playerAge

import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlinx.datetime.*
import kotlin.reflect.KClass

class PlayerAgeDefinitionEvaluator(private val playerDetailsRepository: IPlayerDetailsRepository) : IPlayerDefinitionEvaluator<PlayerAgeDefinition> {
    override val definition: KClass<PlayerAgeDefinition> = PlayerAgeDefinition::class

    override suspend fun evaluate(
        playerId: String,
        condition: PlayerAgeDefinition
    ): Boolean {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null) ?: return false

        val playerBirthDate = playerDetails.birthDate ?: return false

        val birthLocalDate = playerBirthDate.toInstant().toKotlinInstant()
            .toLocalDateTime(TimeZone.UTC).date

        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

        val playerAge = birthLocalDate.until(today, DateTimeUnit.YEAR)

        return condition.value.check(playerAge)
    }
}
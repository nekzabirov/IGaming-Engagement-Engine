package com.nekgambling.segment.condition.playerAge

import com.nekgambling.player.query.FindPlayerDetailsQuery
import com.nekgambling.segment.condition.IConditionEvaluator
import kotlinx.datetime.*
import kotlin.reflect.KClass

class PlayerAgeConditionEvaluator(private val findPlayerDetailsQuery: FindPlayerDetailsQuery) : IConditionEvaluator<PlayerAgeCondition> {
    override val condition: KClass<PlayerAgeCondition> = PlayerAgeCondition::class

    override suspend fun evaluate(
        playerId: String,
        condition: PlayerAgeCondition
    ): Boolean {
        val playerDetails = findPlayerDetailsQuery.execute(playerId).getOrElse { return false }

        val playerBirthDate = playerDetails.birthDate ?: return false

        val birthLocalDate = playerBirthDate.toInstant().toKotlinInstant()
            .toLocalDateTime(TimeZone.UTC).date

        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

        val playerAge = birthLocalDate.until(today, DateTimeUnit.YEAR)

        return condition.vale.check(playerAge)
    }
}
package com.nekgambling.infrastructure.condition.playerAge

import com.nekgambling.application.query.player.FindPlayerDetailsQuery
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlinx.datetime.*
import kotlin.reflect.KClass

class PlayerAgeConditionRuleEvaluator(private val findPlayerDetailsQuery: FindPlayerDetailsQuery) : IConditionRuleEvaluator<PlayerAgeConditionRule> {
    override val condition: KClass<PlayerAgeConditionRule> = PlayerAgeConditionRule::class

    override suspend fun evaluate(
        playerId: String,
        condition: PlayerAgeConditionRule
    ): Boolean {
        val playerDetails = findPlayerDetailsQuery.execute(playerId).getOrElse { return false }

        val playerBirthDate = playerDetails.birthDate ?: return false

        val birthLocalDate = playerBirthDate.toInstant().toKotlinInstant()
            .toLocalDateTime(TimeZone.UTC).date

        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

        val playerAge = birthLocalDate.until(today, DateTimeUnit.YEAR)

        return condition.value.check(playerAge)
    }
}
package com.nekgambling.infrastructure.journey.player.playerAge

import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.player.IPlayerJourneyNodeProcess
import kotlinx.datetime.*
import kotlin.reflect.KClass

class PlayerAgePlayerJourneyNodeProcess(
    private val playerDetailsRepository: IPlayerDetailsRepository,
) : IPlayerJourneyNodeProcess<PlayerAgePlayerJourneyNode> {

    override val nodeType: KClass<PlayerAgePlayerJourneyNode> = PlayerAgePlayerJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: PlayerAgePlayerJourneyNode,
        payload: Map<String, Any>,
    ): JourneyNodeProcess.Response {
        val playerDetails = playerDetailsRepository.findById(playerId).orElse(null)

        val matched = if (playerDetails != null) {
            val playerBirthDate = playerDetails.birthDate
            if (playerBirthDate != null) {
                val birthLocalDate = playerBirthDate.toInstant().toKotlinInstant()
                    .toLocalDateTime(TimeZone.UTC).date
                val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val playerAge = birthLocalDate.until(today, DateTimeUnit.YEAR)
                node.age.check(playerAge)
            } else false
        } else false

        return JourneyNodeProcess.Response(
            nextNode = if (matched) node.matchNode else node.notMatchNode,
            output = emptyMap(),
        )
    }
}

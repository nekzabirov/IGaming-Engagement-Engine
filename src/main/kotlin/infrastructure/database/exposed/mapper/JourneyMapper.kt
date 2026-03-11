package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.infrastructure.database.exposed.entity.JourneyEntity

class JourneyMapper(private val registry: JourneyNodeMapperRegistry) {

    fun toDomain(journeyEntity: JourneyEntity): Journey =
        Journey(
            id = journeyEntity.id.value,
            identity = journeyEntity.identity,
            head = registry.toDomain(journeyEntity.head!!),
        )
}

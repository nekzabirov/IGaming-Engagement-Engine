package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity

class JourneyNodeMapperRegistry(
    mappers: List<IJourneyNodeMapper<*>>,
) {
    private val byIdentity = mappers.associateBy { it.identity }

    fun toDomain(entity: JourneyNodeEntity): IJourneyNode {
        val mapper = byIdentity[entity.type]
            ?: error("No mapper found for journey node type: ${entity.type}")
        return mapper.toDomain(entity) { child -> child?.let { toDomain(it) } }
    }
}

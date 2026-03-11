package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity

class JourneyNodeMapperRegistry(
    mappers: List<IJourneyNodeMapper<*>>,
) {
    private val byIdentity = mappers.associateBy { it.identity }
    private val all = mappers

    fun toDomain(entity: JourneyNodeEntity): IJourneyNode {
        val mapper = byIdentity[entity.type]
            ?: error("No mapper found for journey node type: ${entity.type}")
        return mapper.toDomain(entity) { child -> child?.let { toDomain(it) } }
    }

    @Suppress("UNCHECKED_CAST")
    fun applyToEntity(entity: JourneyNodeEntity, node: IJourneyNode) {
        val mapper = all.find { it.nodeType.isInstance(node) }
            ?: error("No mapper for node: ${node::class}")
        entity.type = mapper.identity
        (mapper as IJourneyNodeMapper<IJourneyNode>).applyToEntity(entity, node)
    }
}

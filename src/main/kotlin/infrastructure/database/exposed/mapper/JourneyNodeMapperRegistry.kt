package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity

class JourneyNodeMapperRegistry(mappers: List<JourneyNodeMapper<*>>) {

    private val mapperByType: Map<String, JourneyNodeMapper<*>> = mappers.associateBy { it.type }

    fun getMapper(type: String): JourneyNodeMapper<IJourneyNode> {
        val mapper = mapperByType[type] ?: error("No JourneyNodeMapper registered for type: $type")
        @Suppress("UNCHECKED_CAST")
        return mapper as JourneyNodeMapper<IJourneyNode>
    }

    fun getMapperForNode(node: IJourneyNode): JourneyNodeMapper<IJourneyNode> {
        val mapper = mapperByType.values.firstOrNull { it.nodeType.isInstance(node) }
            ?: error("No JourneyNodeMapper registered for node: ${node::class.simpleName}")
        @Suppress("UNCHECKED_CAST")
        return mapper as JourneyNodeMapper<IJourneyNode>
    }

    fun toDomain(entity: JourneyNodeEntity, next: IJourneyNode?, notMatchNode: IJourneyNode?): IJourneyNode =
        getMapper(entity.type).toDomain(entity, next, notMatchNode)

    fun applyToEntity(node: IJourneyNode, entity: JourneyNodeEntity) =
        getMapperForNode(node).applyToEntity(node, entity)

    fun resolveType(node: IJourneyNode): String =
        getMapperForNode(node).type
}

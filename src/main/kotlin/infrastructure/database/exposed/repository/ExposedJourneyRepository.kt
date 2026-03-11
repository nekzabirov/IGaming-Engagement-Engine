package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.infrastructure.database.exposed.entity.JourneyEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapperRegistry
import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedJourneyRepository(
    private val database: Database,
    private val mapperRegistry: JourneyNodeMapperRegistry,
) : IJourneyRepository {

    override suspend fun findOfNode(node: IJourneyNode): Journey {
        return newSuspendedTransaction(db = database) {
            val nodeEntity = JourneyNodeEntity[node.id]
            val journeyEntity = nodeEntity.journey

            val headNode = resolveNodeGraph(journeyEntity)
            Journey(
                id = journeyEntity.id.value,
                identity = journeyEntity.identity,
                head = headNode,
            )
        }
    }

    private fun resolveNodeGraph(journeyEntity: JourneyEntity): IJourneyNode {
        val allEntities = JourneyNodeEntity.find {
            JourneyNodesTable.journeyId eq journeyEntity.id
        }.associateBy { it.id.value }

        val resolved = mutableMapOf<Long, IJourneyNode>()

        fun resolve(entityId: Long?): IJourneyNode? {
            if (entityId == null) return null
            resolved[entityId]?.let { return it }

            val entity = allEntities[entityId] ?: error("Node $entityId not found in journey ${journeyEntity.id.value}")
            val next = resolve(entity.next?.id?.value)
            val notMatchNode = resolve(entity.notMatchNode?.id?.value)

            val node = mapperRegistry.toDomain(entity, next, notMatchNode)
            resolved[entityId] = node
            return node
        }

        return resolve(journeyEntity.head.id.value)
            ?: error("Head node not found for journey ${journeyEntity.id.value}")
    }
}

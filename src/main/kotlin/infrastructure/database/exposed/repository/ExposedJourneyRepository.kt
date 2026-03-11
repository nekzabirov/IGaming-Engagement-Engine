package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.infrastructure.database.exposed.entity.JourneyEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyInstantEntity
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyMapper
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapperRegistry
import com.nekgambling.infrastructure.database.exposed.table.JourneyInstantsTable
import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import com.nekgambling.infrastructure.database.exposed.table.JourneysTable
import com.nekgambling.infrastructure.journey.condition.IConditionJourneyNode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedJourneyRepository(
    private val database: Database,
    private val journeyMapper: JourneyMapper,
    private val mapperRegistry: JourneyNodeMapperRegistry,
) : IJourneyRepository {

    override suspend fun findOfNode(node: IJourneyNode): Journey {
        return newSuspendedTransaction(db = database) {
            val nodeEntity = JourneyNodeEntity[node.id]
            val journeyEntity = nodeEntity.journey
            journeyMapper.toDomain(journeyEntity)
        }
    }

    override suspend fun findById(id: Long): Journey? {
        return newSuspendedTransaction(db = database) {
            JourneyEntity.findById(id)?.let { journeyMapper.toDomain(it) }
        }
    }

    override suspend fun findAll(): List<Journey> {
        return newSuspendedTransaction(db = database) {
            JourneyEntity.all().map { journeyMapper.toDomain(it) }
        }
    }

    override suspend fun save(journey: Journey): Journey {
        return newSuspendedTransaction(db = database) {
            if (journey.id == Long.MIN_VALUE) {
                createJourney(journey)
            } else {
                updateJourney(journey)
            }
        }
    }

    override suspend fun delete(id: Long) {
        newSuspendedTransaction(db = database) {
            val journeyEntity = JourneyEntity.findById(id) ?: return@newSuspendedTransaction

            // Delete journey instants
            JourneyInstantEntity.find { JourneyInstantsTable.journey eq id }.forEach { it.delete() }

            // Null out head to break FK
            journeyEntity.head = null

            // Null out all next/notMatchNode FKs to break circular refs
            val nodeEntities = JourneyNodeEntity.find { JourneyNodesTable.journeyId eq id }
            nodeEntities.forEach { node ->
                node.next = null
                node.notMatchNode = null
            }

            // Delete all nodes
            nodeEntities.forEach { it.delete() }

            // Delete journey
            journeyEntity.delete()
        }
    }

    private fun createJourney(journey: Journey): Journey {
        // Create journey with null head
        val journeyEntity = JourneyEntity.new {
            identity = journey.identity
            head = null
        }

        // Collect all nodes via BFS
        val allNodes = collectAllNodes(journey.head)

        // Pass 1: create all node entities with null links
        val nodeToEntity = mutableMapOf<IJourneyNode, JourneyNodeEntity>()
        for (node in allNodes) {
            val entity = JourneyNodeEntity.new {
                this.journey = journeyEntity
                this.next = null
                this.notMatchNode = null
                this.type = "" // will be set by applyToEntity
            }
            mapperRegistry.applyToEntity(entity, node)
            nodeToEntity[node] = entity
        }

        // Pass 2: wire next and notMatchNode FK references
        for (node in allNodes) {
            val entity = nodeToEntity[node]!!
            entity.next = node.next?.let { nodeToEntity[it] }
            if (node is IConditionJourneyNode) {
                entity.notMatchNode = node.notMatchNode?.let { nodeToEntity[it] }
            }
        }

        // Set head
        journeyEntity.head = nodeToEntity[journey.head]

        return journeyMapper.toDomain(journeyEntity)
    }

    private fun updateJourney(journey: Journey): Journey {
        val journeyEntity = JourneyEntity[journey.id]

        // Delete old instants
        JourneyInstantEntity.find { JourneyInstantsTable.journey eq journey.id }.forEach { it.delete() }

        // Null out head
        journeyEntity.head = null

        // Null out all next/notMatchNode FKs
        val oldNodes = JourneyNodeEntity.find { JourneyNodesTable.journeyId eq journey.id }
        oldNodes.forEach { node ->
            node.next = null
            node.notMatchNode = null
        }

        // Delete old nodes
        oldNodes.forEach { it.delete() }

        // Re-create nodes (same as create logic)
        val allNodes = collectAllNodes(journey.head)

        val nodeToEntity = mutableMapOf<IJourneyNode, JourneyNodeEntity>()
        for (node in allNodes) {
            val entity = JourneyNodeEntity.new {
                this.journey = journeyEntity
                this.next = null
                this.notMatchNode = null
                this.type = ""
            }
            mapperRegistry.applyToEntity(entity, node)
            nodeToEntity[node] = entity
        }

        for (node in allNodes) {
            val entity = nodeToEntity[node]!!
            entity.next = node.next?.let { nodeToEntity[it] }
            if (node is IConditionJourneyNode) {
                entity.notMatchNode = node.notMatchNode?.let { nodeToEntity[it] }
            }
        }

        journeyEntity.head = nodeToEntity[journey.head]
        journeyEntity.identity = journey.identity

        return journeyMapper.toDomain(journeyEntity)
    }

    private fun collectAllNodes(head: IJourneyNode): List<IJourneyNode> {
        val visited = mutableSetOf<IJourneyNode>()
        val result = mutableListOf<IJourneyNode>()
        val queue = ArrayDeque<IJourneyNode>()
        queue.add(head)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (node in visited) continue
            visited.add(node)
            result.add(node)

            node.next?.let { queue.add(it) }
            if (node is IConditionJourneyNode) {
                node.matchNode?.let { queue.add(it) }
                node.notMatchNode?.let { queue.add(it) }
            }
        }

        return result
    }
}

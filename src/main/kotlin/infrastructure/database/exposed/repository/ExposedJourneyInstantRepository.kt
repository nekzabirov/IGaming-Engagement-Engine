package com.nekgambling.infrastructure.database.exposed.repository

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.journey.Journey
import com.nekgambling.domain.model.journey.JourneyInstant
import com.nekgambling.domain.repository.IJourneyInstantRepository
import com.nekgambling.infrastructure.database.exposed.entity.*
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapperRegistry
import com.nekgambling.infrastructure.database.exposed.table.JourneyInstantsTable
import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Optional

class ExposedJourneyInstantRepository(
    private val database: Database,
    private val mapperRegistry: JourneyNodeMapperRegistry,
) : IJourneyInstantRepository {

    override suspend fun findBy(playerId: String, journey: Journey): Optional<JourneyInstant> {
        return newSuspendedTransaction(db = database) {
            val entity = JourneyInstantEntity.find {
                (JourneyInstantsTable.playerId eq playerId) and
                    (JourneyInstantsTable.journey eq journey.id)
            }.firstOrNull()

            if (entity == null) {
                Optional.empty()
            } else {
                val currentNode = resolveNode(entity.currentNode, journey)
                Optional.of(
                    JourneyInstant(
                        id = entity.id.value,
                        playerId = entity.playerId,
                        journey = journey,
                        currentNode = currentNode,
                        payload = deserializePayload(entity.payload),
                    )
                )
            }
        }
    }

    override suspend fun save(journeyInstant: JourneyInstant): JourneyInstant {
        return newSuspendedTransaction(db = database) {
            if (journeyInstant.id == Long.MIN_VALUE) {
                val entity = JourneyInstantEntity.new {
                    playerId = journeyInstant.playerId
                    journey = JourneyEntity[journeyInstant.journey.id]
                    currentNode = JourneyNodeEntity[journeyInstant.currentNode.id]
                    payload = serializePayload(journeyInstant.payload)
                }
                journeyInstant.copy(id = entity.id.value)
            } else {
                val entity = JourneyInstantEntity[journeyInstant.id]
                entity.currentNode = JourneyNodeEntity[journeyInstant.currentNode.id]
                entity.payload = serializePayload(journeyInstant.payload)
                journeyInstant
            }
        }
    }

    override suspend fun delete(journeyInstant: JourneyInstant) {
        newSuspendedTransaction(db = database) {
            JourneyInstantEntity[journeyInstant.id].delete()
        }
    }

    private fun resolveNode(nodeEntity: JourneyNodeEntity, journey: Journey): IJourneyNode {
        fun findInGraph(node: IJourneyNode?, targetId: Long): IJourneyNode? {
            if (node == null) return null
            if (node.id == targetId) return node
            return findInGraph(node.next, targetId)
        }

        return findInGraph(journey.head, nodeEntity.id.value)
            ?: rebuildNode(nodeEntity)
    }

    private fun rebuildNode(nodeEntity: JourneyNodeEntity): IJourneyNode {
        val allEntities = JourneyNodeEntity.find {
            JourneyNodesTable.journeyId eq nodeEntity.journey.id
        }.associateBy { it.id.value }

        val resolved = mutableMapOf<Long, IJourneyNode>()

        fun resolve(entityId: Long?): IJourneyNode? {
            if (entityId == null) return null
            resolved[entityId]?.let { return it }
            val entity = allEntities[entityId] ?: return null
            val node = mapperRegistry.toDomain(entity)
            resolved[entityId] = node
            return node
        }

        return resolve(nodeEntity.id.value)
            ?: error("Failed to resolve node ${nodeEntity.id.value}")
    }
}

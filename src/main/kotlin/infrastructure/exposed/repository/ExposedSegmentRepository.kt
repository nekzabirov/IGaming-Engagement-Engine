package com.nekgambling.infrastructure.exposed.repository

import com.nekgambling.domain.segment.model.SegmentResult
import com.nekgambling.domain.segment.repository.ISegmentRepository
import com.nekgambling.infrastructure.exposed.table.SegmentResultsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.upsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedSegmentRepository(
    private val database: Database,
) : ISegmentRepository {

    override suspend fun save(result: SegmentResult): SegmentResult = dbQuery {
        SegmentResultsTable.upsert {
            it[playerId] = result.playerId
            it[segmentId] = result.segment.id
            it[passed] = result.passed
            it[updatedAt] = result.updatedAt
        }
        result
    }

    private suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(db = database) { block() }
}

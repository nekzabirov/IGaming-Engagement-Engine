package com.nekgambling.infrastructure.exposed.repository

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.domain.condition.model.ConditionResult
import com.nekgambling.domain.condition.repository.IConditionRepository
import com.nekgambling.infrastructure.exposed.table.ConditionResultsTable
import com.nekgambling.infrastructure.exposed.table.ConditionsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Optional

class ExposedConditionRepository(
    private val database: Database,
) : IConditionRepository {

    override suspend fun save(condition: Condition): Condition = newSuspendedTransaction(db = database) {
        if (condition.id == Long.MIN_VALUE) {
            val generatedId = ConditionsTable.insertAndGetId {
                it[rule] = condition.rule
            }.value

            condition.copy(id = generatedId)
        } else {
            ConditionsTable.update({ ConditionsTable.id eq condition.id }) {
                it[rule] = condition.rule
            }
            condition
        }
    }

    override suspend fun save(conditionResult: ConditionResult): ConditionResult = newSuspendedTransaction(db = database) {
        ConditionResultsTable.upsert {
            it[playerId] = conditionResult.playerId
            it[condition] = conditionResult.condition.id
            it[passed] = conditionResult.passed
            it[updatedAt] = conditionResult.updatedAt
        }
        conditionResult
    }

    override suspend fun findResultBy(playerId: String, conditionId: Long): Optional<ConditionResult> = newSuspendedTransaction(db = database) {
        ConditionResultsTable
            .join(ConditionsTable, JoinType.INNER, ConditionResultsTable.condition, ConditionsTable.id)
            .selectAll()
            .where {
                (ConditionResultsTable.playerId eq playerId) and
                    (ConditionResultsTable.condition eq conditionId)
            }
            .firstOrNull()
            ?.let { row ->
                ConditionResult(
                    condition = Condition(
                        id = row[ConditionsTable.id].value,
                        rule = row[ConditionsTable.rule],
                    ),
                    playerId = row[ConditionResultsTable.playerId],
                    passed = row[ConditionResultsTable.passed],
                    updatedAt = row[ConditionResultsTable.updatedAt],
                )
            }
            .let { Optional.ofNullable(it) }
    }
}

package com.nekgambling.infrastructure.exposed.entity

import com.nekgambling.domain.condition.model.Condition
import com.nekgambling.infrastructure.exposed.table.ConditionsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ConditionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ConditionEntity>(ConditionsTable)

    var rule by ConditionsTable.rule

    fun toDomain() = Condition(
        id = id.value,
        rule = rule,
    )
}

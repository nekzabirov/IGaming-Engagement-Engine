package com.nekgambling.infrastructure.exposed.table

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.infrastructure.exposed.conditionRuleJson
import kotlinx.serialization.PolymorphicSerializer
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb

object ConditionsTable : LongIdTable("conditions") {
    val rule = jsonb<IConditionRule>("rule", conditionRuleJson, PolymorphicSerializer(IConditionRule::class))
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.condition.*
import kotlin.reflect.KClass

object ConditionNodeMapper : IJourneyNodeMapper<IConditionJourneyNode> {
    override val nodeType: KClass<IConditionJourneyNode> = IConditionJourneyNode::class
    override val identity: String = "condition"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): IConditionJourneyNode {
        val id = entity.id.value
        val inputKey = entity.inputKey!!
        val matchNode = resolveNode(entity.next)
        val notMatchNode = resolveNode(entity.notMatchNode)

        return when (entity.subType) {
            "bool" -> BoolConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                expected = entity.expected!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            "numberInRange" -> NumberInRangeConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                min = entity.rangeMin!!,
                max = entity.rangeMax!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            "numberMoreThan" -> NumberMoreThanConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                threshold = entity.threshold!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            "numberLessThan" -> NumberLessThanConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                threshold = entity.threshold!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            "numberEqual" -> NumberEqualConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                target = entity.targetNumber!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            "stringEqual" -> StringEqualConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                target = entity.targetString!!,
                matchNode = matchNode,
                notMatchNode = notMatchNode,
            )
            else -> error("Unknown condition subType: ${entity.subType}")
        }
    }

    override fun applyToEntity(entity: JourneyNodeEntity, node: IConditionJourneyNode) {
        entity.inputKey = node.inputKey
        when (node) {
            is BoolConditionJourneyNode -> {
                entity.subType = "bool"
                entity.expected = node.expected
            }
            is NumberInRangeConditionJourneyNode -> {
                entity.subType = "numberInRange"
                entity.rangeMin = node.min
                entity.rangeMax = node.max
            }
            is NumberMoreThanConditionJourneyNode -> {
                entity.subType = "numberMoreThan"
                entity.threshold = node.threshold
            }
            is NumberLessThanConditionJourneyNode -> {
                entity.subType = "numberLessThan"
                entity.threshold = node.threshold
            }
            is NumberEqualConditionJourneyNode -> {
                entity.subType = "numberEqual"
                entity.targetNumber = node.target
            }
            is StringEqualConditionJourneyNode -> {
                entity.subType = "stringEqual"
                entity.targetString = node.target
            }
        }
    }
}

package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.condition.*
import kotlin.reflect.KClass

object ConditionNodeMapper : JourneyNodeMapper<IConditionJourneyNode> {
    override val type: String = "condition"
    override val nodeType: KClass<out IConditionJourneyNode> = IConditionJourneyNode::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): IConditionJourneyNode {
        val id = entity.id.value
        val inputKey = entity.inputKey!!

        return when (entity.subType) {
            "bool" -> BoolConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                expected = entity.expected!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            "numberInRange" -> NumberInRangeConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                min = entity.rangeMin!!,
                max = entity.rangeMax!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            "numberMoreThan" -> NumberMoreThanConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                threshold = entity.threshold!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            "numberLessThan" -> NumberLessThanConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                threshold = entity.threshold!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            "numberEqual" -> NumberEqualConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                target = entity.targetNumber!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            "stringEqual" -> StringEqualConditionJourneyNode(
                id = id,
                inputKey = inputKey,
                target = entity.targetString!!,
                matchNode = next,
                notMatchNode = notMatchNode,
            )
            else -> error("Unknown condition subType: ${entity.subType}")
        }
    }

    override fun applyToEntity(node: IConditionJourneyNode, entity: JourneyNodeEntity) {
        entity.inputKey = node.inputKey
        when (node) {
            is BoolConditionJourneyNode -> {
                entity.subType = "bool"
                entity.expected = node.expected
            }
            is NumberInRangeConditionJourneyNode -> {
                entity.subType = "numberInRange"
                entity.rangeMin = node.min.toDouble()
                entity.rangeMax = node.max.toDouble()
            }
            is NumberMoreThanConditionJourneyNode -> {
                entity.subType = "numberMoreThan"
                entity.threshold = node.threshold.toDouble()
            }
            is NumberLessThanConditionJourneyNode -> {
                entity.subType = "numberLessThan"
                entity.threshold = node.threshold.toDouble()
            }
            is NumberEqualConditionJourneyNode -> {
                entity.subType = "numberEqual"
                entity.targetNumber = node.target.toDouble()
            }
            is StringEqualConditionJourneyNode -> {
                entity.subType = "stringEqual"
                entity.targetString = node.target
            }
            else -> error("Unknown condition node type: ${node::class.simpleName}")
        }
    }
}

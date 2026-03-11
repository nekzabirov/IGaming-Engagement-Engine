package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import kotlin.reflect.KClass

object ConditionJourneyNodeNomenclature : JourneyNodeNomenclature<IConditionJourneyNode> {
    override val nodeType: KClass<IConditionJourneyNode> = IConditionJourneyNode::class

    override val identity: String = "condition"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun toAssetsMap(node: IConditionJourneyNode): Map<String, Any> = buildMap {
        put("inputKey", node.inputKey)
        when (node) {
            is BoolConditionJourneyNode -> {
                put("type", "bool")
                put("expected", node.expected)
            }
            is NumberInRangeConditionJourneyNode -> {
                put("type", "numberInRange")
                put("min", node.min)
                put("max", node.max)
            }
            is NumberMoreThanConditionJourneyNode -> {
                put("type", "numberMoreThan")
                put("threshold", node.threshold)
            }
            is NumberLessThanConditionJourneyNode -> {
                put("type", "numberLessThan")
                put("threshold", node.threshold)
            }
            is NumberEqualConditionJourneyNode -> {
                put("type", "numberEqual")
                put("target", node.target)
            }
            is StringEqualConditionJourneyNode -> {
                put("type", "stringEqual")
                put("target", node.target)
            }
            else -> error("Unknown condition node type: ${node::class}")
        }
    }

    override fun fromAssetsMap(map: Map<String, Any>): IConditionJourneyNode {
        val inputKey = map["inputKey"] as String
        return when (map["type"]) {
            "bool" -> BoolConditionJourneyNode(
                inputKey = inputKey,
                expected = map["expected"] as Boolean,
            )
            "numberInRange" -> NumberInRangeConditionJourneyNode(
                inputKey = inputKey,
                min = map["min"] as Number,
                max = map["max"] as Number,
            )
            "numberMoreThan" -> NumberMoreThanConditionJourneyNode(
                inputKey = inputKey,
                threshold = map["threshold"] as Number,
            )
            "numberLessThan" -> NumberLessThanConditionJourneyNode(
                inputKey = inputKey,
                threshold = map["threshold"] as Number,
            )
            "numberEqual" -> NumberEqualConditionJourneyNode(
                inputKey = inputKey,
                target = map["target"] as Number,
            )
            "stringEqual" -> StringEqualConditionJourneyNode(
                inputKey = inputKey,
                target = map["target"] as String,
            )
            else -> error("Unknown condition type: ${map["type"]}")
        }
    }
}

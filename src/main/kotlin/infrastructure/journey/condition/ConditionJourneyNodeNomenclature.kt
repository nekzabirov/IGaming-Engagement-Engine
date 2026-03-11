package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.strategy.*
import kotlin.reflect.KClass

object ConditionJourneyNodeNomenclature : JourneyNodeNomenclature<IConditionJourneyNode> {
    override val nodeType: KClass<IConditionJourneyNode> = IConditionJourneyNode::class

    override val identity: String = "condition"

    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(
            name = "type", type = ParamType.ENUM, required = true,
            enumValues = listOf("bool", "numberInRange", "numberMoreThan", "numberLessThan", "numberEqual", "stringEqual"),
            subtypes = listOf(
                SubtypeDescriptor(
                    discriminatorValue = "bool",
                    assets = listOf(AssetParamDescriptor(name = "expected", type = ParamType.BOOLEAN, required = true)),
                ),
                SubtypeDescriptor(
                    discriminatorValue = "numberInRange",
                    assets = listOf(
                        AssetParamDescriptor(name = "min", type = ParamType.DOUBLE, required = true),
                        AssetParamDescriptor(name = "max", type = ParamType.DOUBLE, required = true),
                    ),
                ),
                SubtypeDescriptor(
                    discriminatorValue = "numberMoreThan",
                    assets = listOf(AssetParamDescriptor(name = "threshold", type = ParamType.DOUBLE, required = true)),
                ),
                SubtypeDescriptor(
                    discriminatorValue = "numberLessThan",
                    assets = listOf(AssetParamDescriptor(name = "threshold", type = ParamType.DOUBLE, required = true)),
                ),
                SubtypeDescriptor(
                    discriminatorValue = "numberEqual",
                    assets = listOf(AssetParamDescriptor(name = "target", type = ParamType.DOUBLE, required = true)),
                ),
                SubtypeDescriptor(
                    discriminatorValue = "stringEqual",
                    assets = listOf(AssetParamDescriptor(name = "target", type = ParamType.STRING, required = true)),
                ),
            ),
        ),
    )

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
                min = (map["min"] as Number).toDouble(),
                max = (map["max"] as Number).toDouble(),
            )
            "numberMoreThan" -> NumberMoreThanConditionJourneyNode(
                inputKey = inputKey,
                threshold = (map["threshold"] as Number).toDouble(),
            )
            "numberLessThan" -> NumberLessThanConditionJourneyNode(
                inputKey = inputKey,
                threshold = (map["threshold"] as Number).toDouble(),
            )
            "numberEqual" -> NumberEqualConditionJourneyNode(
                inputKey = inputKey,
                target = (map["target"] as Number).toDouble(),
            )
            "stringEqual" -> StringEqualConditionJourneyNode(
                inputKey = inputKey,
                target = map["target"] as String,
            )
            else -> error("Unknown condition type: ${map["type"]}")
        }
    }
}

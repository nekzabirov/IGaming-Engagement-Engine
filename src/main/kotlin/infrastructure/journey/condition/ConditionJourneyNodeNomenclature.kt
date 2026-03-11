package com.nekgambling.infrastructure.journey.condition

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.*
import kotlin.reflect.KClass

object BoolConditionNomenclature : JourneyNodeNomenclature<BoolConditionJourneyNode> {
    override val nodeType: KClass<BoolConditionJourneyNode> = BoolConditionJourneyNode::class
    override val identity: String = "boolCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "expected", type = ParamType.BOOLEAN, required = true),
    )

    override fun toAssetsMap(node: BoolConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "expected" to node.expected,
    )

    override fun fromAssetsMap(map: Map<String, Any>): BoolConditionJourneyNode = BoolConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        expected = map["expected"] as Boolean,
    )

    override fun withLinks(node: BoolConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): BoolConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

object NumberInRangeConditionNomenclature : JourneyNodeNomenclature<NumberInRangeConditionJourneyNode> {
    override val nodeType: KClass<NumberInRangeConditionJourneyNode> = NumberInRangeConditionJourneyNode::class
    override val identity: String = "numberInRangeCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "min", type = ParamType.NUMBER, required = true),
        AssetParamDescriptor(name = "max", type = ParamType.NUMBER, required = true),
    )

    override fun toAssetsMap(node: NumberInRangeConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "min" to node.min,
        "max" to node.max,
    )

    override fun fromAssetsMap(map: Map<String, Any>): NumberInRangeConditionJourneyNode = NumberInRangeConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        min = (map["min"] as Number).toDouble(),
        max = (map["max"] as Number).toDouble(),
    )

    override fun withLinks(node: NumberInRangeConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): NumberInRangeConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

object NumberMoreThanConditionNomenclature : JourneyNodeNomenclature<NumberMoreThanConditionJourneyNode> {
    override val nodeType: KClass<NumberMoreThanConditionJourneyNode> = NumberMoreThanConditionJourneyNode::class
    override val identity: String = "numberMoreThanCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "threshold", type = ParamType.NUMBER, required = true),
    )

    override fun toAssetsMap(node: NumberMoreThanConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "threshold" to node.threshold,
    )

    override fun fromAssetsMap(map: Map<String, Any>): NumberMoreThanConditionJourneyNode = NumberMoreThanConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        threshold = (map["threshold"] as Number).toDouble(),
    )

    override fun withLinks(node: NumberMoreThanConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): NumberMoreThanConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

object NumberLessThanConditionNomenclature : JourneyNodeNomenclature<NumberLessThanConditionJourneyNode> {
    override val nodeType: KClass<NumberLessThanConditionJourneyNode> = NumberLessThanConditionJourneyNode::class
    override val identity: String = "numberLessThanCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "threshold", type = ParamType.NUMBER, required = true),
    )

    override fun toAssetsMap(node: NumberLessThanConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "threshold" to node.threshold,
    )

    override fun fromAssetsMap(map: Map<String, Any>): NumberLessThanConditionJourneyNode = NumberLessThanConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        threshold = (map["threshold"] as Number).toDouble(),
    )

    override fun withLinks(node: NumberLessThanConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): NumberLessThanConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

object NumberEqualConditionNomenclature : JourneyNodeNomenclature<NumberEqualConditionJourneyNode> {
    override val nodeType: KClass<NumberEqualConditionJourneyNode> = NumberEqualConditionJourneyNode::class
    override val identity: String = "numberEqualCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "target", type = ParamType.NUMBER, required = true),
    )

    override fun toAssetsMap(node: NumberEqualConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "target" to node.target,
    )

    override fun fromAssetsMap(map: Map<String, Any>): NumberEqualConditionJourneyNode = NumberEqualConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        target = (map["target"] as Number).toDouble(),
    )

    override fun withLinks(node: NumberEqualConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): NumberEqualConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

object StringEqualConditionNomenclature : JourneyNodeNomenclature<StringEqualConditionJourneyNode> {
    override val nodeType: KClass<StringEqualConditionJourneyNode> = StringEqualConditionJourneyNode::class
    override val identity: String = "stringEqualCondition"
    override val category: NodeCategory = NodeCategory.CONDITION

    override fun inputParams(): Set<String> = emptySet()
    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "inputKey", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "target", type = ParamType.STRING, required = true),
    )

    override fun toAssetsMap(node: StringEqualConditionJourneyNode): Map<String, Any> = mapOf(
        "inputKey" to node.inputKey,
        "target" to node.target,
    )

    override fun fromAssetsMap(map: Map<String, Any>): StringEqualConditionJourneyNode = StringEqualConditionJourneyNode(
        inputKey = map["inputKey"] as String,
        target = map["target"] as String,
    )

    override fun withLinks(node: StringEqualConditionJourneyNode, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): StringEqualConditionJourneyNode =
        node.copy(matchNode = matchNode, notMatchNode = notMatchNode)
}

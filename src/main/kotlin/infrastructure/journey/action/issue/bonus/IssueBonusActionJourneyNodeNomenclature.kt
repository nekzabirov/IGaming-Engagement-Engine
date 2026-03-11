package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.ParamType
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object IssueFixedBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueFixedBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueFixedBonusActionJourneyNode> = IssueFixedBonusActionJourneyNode::class

    override val identity: String = "issueFixedBonusAction"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "bonusIdentity", type = ParamType.STRING, required = true),
        AssetParamDescriptor(name = "currency", type = ParamType.CURRENCY, required = true),
        AssetParamDescriptor(name = "amount", type = ParamType.LONG, required = true),
    )

    override fun toAssetsMap(node: IssueFixedBonusActionJourneyNode): Map<String, Any> = mapOf(
        "bonusIdentity" to node.bonusIdentity,
        "currency" to node.currency.code,
        "amount" to node.amount,
    )

    override fun fromAssetsMap(map: Map<String, Any>): IssueFixedBonusActionJourneyNode = IssueFixedBonusActionJourneyNode(
        bonusIdentity = map["bonusIdentity"] as String,
        currency = Currency(map["currency"] as String),
        amount = (map["amount"] as Number).toLong(),
    )
}

object IssueDynamicBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueDynamicBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueDynamicBonusActionJourneyNode> = IssueDynamicBonusActionJourneyNode::class

    override val identity: String = "issueDynamicBonusAction"

    override fun inputParams(): Set<String> = setOf("currency", "amount")

    override fun outputParams(): Set<String> = emptySet()

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "bonusIdentity", type = ParamType.STRING, required = true),
    )

    override fun toAssetsMap(node: IssueDynamicBonusActionJourneyNode): Map<String, Any> = mapOf(
        "bonusIdentity" to node.bonusIdentity,
    )

    override fun fromAssetsMap(map: Map<String, Any>): IssueDynamicBonusActionJourneyNode = IssueDynamicBonusActionJourneyNode(
        bonusIdentity = map["bonusIdentity"] as String,
    )
}

package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object IssueFixedBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueFixedBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueFixedBonusActionJourneyNode> = IssueFixedBonusActionJourneyNode::class

    override val identity: String = "issueFixedBonusAction"

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()

    override fun toAssetsMap(node: IssueFixedBonusActionJourneyNode): Map<String, Any> = mapOf(
        "identity" to node.identity,
        "currency" to node.currency.code,
        "amount" to node.amount,
    )

    override fun fromAssetsMap(map: Map<String, Any>): IssueFixedBonusActionJourneyNode = IssueFixedBonusActionJourneyNode(
        identity = map["identity"] as String,
        currency = Currency(map["currency"] as String),
        amount = (map["amount"] as Number).toLong(),
    )
}

object IssueDynamicBonusActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<IssueDynamicBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueDynamicBonusActionJourneyNode> = IssueDynamicBonusActionJourneyNode::class

    override val identity: String = "issueDynamicBonusAction"

    override fun inputParams(): Set<String> = setOf("currency", "amount")

    override fun outputParams(): Set<String> = emptySet()

    override fun toAssetsMap(node: IssueDynamicBonusActionJourneyNode): Map<String, Any> = mapOf(
        "identity" to node.identity,
    )

    override fun fromAssetsMap(map: Map<String, Any>): IssueDynamicBonusActionJourneyNode = IssueDynamicBonusActionJourneyNode(
        identity = map["identity"] as String,
    )
}

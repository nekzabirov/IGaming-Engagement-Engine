package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.action.IActionJourneyNodeProcess
import kotlin.reflect.KClass

class IssueBonusActionJourneyNodeProcess : IActionJourneyNodeProcess<IssueBonusActionJourneyNode>() {
    override val nodeType: KClass<IssueBonusActionJourneyNode> = IssueBonusActionJourneyNode::class

    override suspend fun process(
        playerId: String,
        node: IssueBonusActionJourneyNode,
        payload: Map<String, Any>
    ): JourneyNodeProcess.Response {
        val (amount, currency) = when (node) {
            is IssueFixedBonusActionJourneyNode -> {
                node.amount to node.currency
            }
            is IssueDynamicBonusActionJourneyNode -> {
                val currency = Currency(payload["currency"] as String)
                val amount = payload.getValue("amount") as Long
                amount to currency
            }
        }

        issueBonus(node.identity, currency, amount)

        return JourneyNodeProcess.Response(
            nextNode = node.next,
            output = emptyMap(),
        )
    }

    private suspend fun issueBonus(identity: String, currency: Currency, amount: Long) {
        TODO("Not yet implemented")
    }
}
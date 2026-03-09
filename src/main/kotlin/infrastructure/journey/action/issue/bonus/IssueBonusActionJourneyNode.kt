package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode

sealed class IssueBonusActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    open val identity: String,
) : IActionJourneyNode(id, next)

data class IssueFixedBonusActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    override val identity: String,
    val currency: Currency,
    val amount: Long,
) : IssueBonusActionJourneyNode(id, next, identity)


data class IssueDynamicBonusActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    override val identity: String,
) : IssueBonusActionJourneyNode(id, next, identity)
package com.nekgambling.infrastructure.journey.action.issue.bonus

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class IssueBonusActionJourneyNode(
    @Transient override val id: Long = Long.MIN_VALUE,
    @Transient override val next: IJourneyNode? = null,
    @Transient open val bonusIdentity: String = "",
) : IActionJourneyNode(id, next)

@Serializable
@SerialName("issueFixedBonus")
data class IssueFixedBonusActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    override val bonusIdentity: String,
    val currency: Currency,
    val amount: Long,
) : IssueBonusActionJourneyNode(id, next, bonusIdentity)


@Serializable
@SerialName("issueDynamicBonus")
data class IssueDynamicBonusActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    override val bonusIdentity: String,
) : IssueBonusActionJourneyNode(id, next, bonusIdentity)

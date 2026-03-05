package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class BonusTriggerJourneyNode(
    val id: String? = null,
    val identity: String? = null,
    val status: PlayerBonus.Status? = null,
    val payoutAmount: NumberParamValue? = null,

    override val prev: IJourneyNode? = null,
    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode {
    override fun requireParams(): Set<String> = setOf("id", "identity", "status", "payoutAmount")

    override fun outputParams(): Set<String> = setOf("id", "identity", "status", "payoutAmount")
}
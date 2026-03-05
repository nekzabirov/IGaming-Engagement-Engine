package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class BonusTriggerJourneyNode(
    val bonusId: String? = null,
    val bonusIdentity: String? = null,
    val bonusStatus: PlayerBonus.Status? = null,
    val bonusPayoutAmount: NumberParamValue? = null,

    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(next = _next) {
    override fun inputParams(): Set<String> = setOf("bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")

    override fun outputParams(): Set<String> = setOf("bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")
}

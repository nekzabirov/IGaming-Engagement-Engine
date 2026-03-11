package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class BonusTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val bonusId: String? = null,
    val bonusIdentity: String? = null,
    val bonusStatus: PlayerBonus.Status? = null,
    val bonusPayoutAmount: NumberParamValue? = null,

    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {
    companion object {
        const val TRIGGER_NAME = "bonus"
    }
}

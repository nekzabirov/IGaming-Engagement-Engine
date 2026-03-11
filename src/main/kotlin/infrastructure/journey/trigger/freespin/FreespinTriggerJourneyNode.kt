package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.asset.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class FreespinTriggerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val freespinId: String? = null,
    val freespinIdentity: String? = null,
    val gameId: String? = null,
    val freespinStatus: PlayerFreespin.Status? = null,
    val freespinPayoutRealAmount: NumberParamValue? = null,

    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = id, next = next) {
    companion object {
        const val TRIGGER_NAME = "freespin"
    }
}

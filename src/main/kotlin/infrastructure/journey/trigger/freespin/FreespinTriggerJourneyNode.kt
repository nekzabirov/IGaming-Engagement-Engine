package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class FreespinTriggerJourneyNode(
    private val _id: Long = Long.MIN_VALUE,
    val freespinId: String? = null,
    val freespinIdentity: String? = null,
    val gameId: String? = null,
    val freespinStatus: PlayerFreespin.Status? = null,
    val freespinPayoutRealAmount: NumberParamValue? = null,

    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(id = _id, next = _next) {
    override fun inputParams(): Set<String> = setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus")

    override fun outputParams(): Set<String> = setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus", "freespinPayoutRealAmount")
}

package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class FreespinTriggerJourneyNode(
    val freespinId: String? = null,
    val freespinIdentity: String? = null,
    val gameId: String? = null,
    val freespinStatus: PlayerFreespin.Status? = null,
    val freespinPayoutRealAmount: NumberParamValue? = null,

    private val _prev: IJourneyNode? = null,
    private val _next: IJourneyNode? = null,
) : ITriggerJourneyNode(prev = _prev, next = _next) {
    override fun inputParams(): Set<String> = setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus")

    override fun outputParams(): Set<String> = setOf("freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus", "freespinPayoutRealAmount")
}

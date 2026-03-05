package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.journey.model.IJourneyNode
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.trigger.ITriggerJourneyNode

data class FreespinTriggerJourneyNode(
    val id: String? = null,
    val identity: String? = null,
    val game: String? = null,
    val status: PlayerFreespin.Status? = null,
    val payoutRealAmount: NumberParamValue? = null,

    override val prev: IJourneyNode? = null,
    override val next: IJourneyNode? = null,
) : ITriggerJourneyNode {
    override fun requireParams(): Set<String> = setOf("id", "identity", "game", "currency", "status")

    override fun outputParams(): Set<String> = setOf("id", "identity", "game", "currency", "status", "payoutRealAmount")
}
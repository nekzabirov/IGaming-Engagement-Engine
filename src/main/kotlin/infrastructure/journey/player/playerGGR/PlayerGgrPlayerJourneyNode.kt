package com.nekgambling.infrastructure.journey.player.playerGGR

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode

data class PlayerGgrPlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val ggr: NumberParamValue? = null,
    val ngr: NumberParamValue? = null,
    val date: DateParamValue,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null,
) : PlayerJourneyNode(id = id, matchNode = matchNode, notMatchNode = notMatchNode)

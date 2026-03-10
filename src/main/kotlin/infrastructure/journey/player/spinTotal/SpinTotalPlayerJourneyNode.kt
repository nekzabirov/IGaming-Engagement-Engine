package com.nekgambling.infrastructure.journey.player.spinTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode

data class SpinTotalPlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val placeAmount: NumberParamValue? = null,
    val settleAmount: NumberParamValue? = null,
    val date: DateParamValue,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null,
) : PlayerJourneyNode(id = id, matchNode = matchNode, notMatchNode = notMatchNode)

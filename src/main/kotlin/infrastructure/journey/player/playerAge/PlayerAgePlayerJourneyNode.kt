package com.nekgambling.infrastructure.journey.player.playerAge

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode

data class PlayerAgePlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val age: NumberParamValue,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null,
) : PlayerJourneyNode(id = id, matchNode = matchNode, notMatchNode = notMatchNode)

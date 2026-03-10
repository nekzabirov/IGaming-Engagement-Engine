package com.nekgambling.infrastructure.journey.player.invoiceTotal

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode

data class InvoiceTotalPlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val depositCount: NumberParamValue? = null,
    val withdrawCount: NumberParamValue? = null,
    val depositAmount: NumberParamValue? = null,
    val withdrawAmount: NumberParamValue? = null,
    val taxAmount: NumberParamValue? = null,
    val feesAmount: NumberParamValue? = null,
    val date: DateParamValue,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null,
) : PlayerJourneyNode(id = id, matchNode = matchNode, notMatchNode = notMatchNode)

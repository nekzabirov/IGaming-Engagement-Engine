package com.nekgambling.infrastructure.journey.player.invoiceTotal

import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import com.nekgambling.domain.vo.param.DateParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("invoiceTotal")
data class InvoiceTotalPlayerDefinition(
    val depositCount: NumberParamValue? = null,
    val withdrawCount: NumberParamValue? = null,

    val depositAmount: NumberParamValue? = null,
    val withdrawAmount: NumberParamValue? = null,

    val taxAmount: NumberParamValue? = null,
    val feesAmount: NumberParamValue? = null,

    val date: DateParamValue,
) : IPlayerDefinition

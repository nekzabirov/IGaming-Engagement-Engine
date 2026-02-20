package com.nekgambling.domain.player.model

sealed interface PlayerTotalInfo

data class PlayerTotalSpinInfo(
    val placeAmount: Long,
    val settleAmount: Long,

    val realPlaceAmount: Long,
    val realSettleAmount: Long,
) {
    fun calculateGGR() =
        placeAmount - settleAmount

    fun calculateRealGRR() =
        realPlaceAmount - settleAmount
}


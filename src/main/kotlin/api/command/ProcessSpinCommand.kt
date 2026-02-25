package com.nekgambling.api.command

data class ProcessSpinCommand(
    val playerId: String,
    val id: String,
    val placeRealAmount: Double,
    val placeBonusAmount: Double,
    val settleRealAmount: Double,
    val settleBonusAmount: Double,
    val gameProvider: String,
    val gameIdentity: String,
    val currency: String,
) : ICommand<Unit>

package com.nekgambling.player.event.player

import com.nekgambling.player.model.PlayerDetails

data class PlayerRegisteredEvent(
    override val playerId: String,
    override val details: PlayerDetails
) : IPlayerEvent

package com.nekgambling.application.event.player.player

import com.nekgambling.domain.player.model.PlayerDetails

data class PlayerRegisteredEvent(
    override val playerId: String,
    override val details: PlayerDetails
) : IPlayerEvent

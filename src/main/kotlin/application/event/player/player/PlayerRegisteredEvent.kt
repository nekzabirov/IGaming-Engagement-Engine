package com.nekgambling.application.event.player.player

import com.nekgambling.domain.model.player.PlayerDetails

data class PlayerRegisteredEvent(
    override val playerId: String,
    override val details: PlayerDetails
) : com.nekgambling.application.event.player.player.IPlayerEvent

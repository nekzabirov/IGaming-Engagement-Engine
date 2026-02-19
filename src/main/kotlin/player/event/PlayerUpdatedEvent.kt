package com.nekgambling.player.event

import com.nekgambling.player.model.PlayerDetails

data class PlayerUpdatedEvent(
    override val playerId: String,
    override val details: PlayerDetails
) : IPlayerEvent

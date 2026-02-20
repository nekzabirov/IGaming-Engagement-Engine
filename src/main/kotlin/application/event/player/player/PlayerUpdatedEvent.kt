package com.nekgambling.application.event.player.player

import com.nekgambling.domain.player.model.PlayerDetails

data class PlayerUpdatedEvent(
    override val playerId: String,
    override val details: PlayerDetails
) : IPlayerEvent

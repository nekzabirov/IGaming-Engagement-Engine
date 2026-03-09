package com.nekgambling.application.event.player.player

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.model.player.PlayerDetails

sealed interface IPlayerEvent : IEventAdapter.AppEvent {
    val playerId: String

    val details: PlayerDetails
}
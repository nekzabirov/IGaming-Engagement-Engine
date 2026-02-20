package com.nekgambling.application.event.player.player

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.player.model.PlayerDetails

sealed interface IPlayerEvent : IEventAdapter.AppEvent {
    val playerId: String

    val details: PlayerDetails
}
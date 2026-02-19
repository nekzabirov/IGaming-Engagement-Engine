package com.nekgambling.player.event

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.model.PlayerDetails

sealed interface IPlayerEvent : IEventAdapter.AppEvent {
    val playerId: String

    val details: PlayerDetails
}
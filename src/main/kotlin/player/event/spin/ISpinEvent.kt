package com.nekgambling.player.event.spin

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.model.PlayerSpin

interface ISpinEvent : IEventAdapter.AppEvent {
    val spin: PlayerSpin
}
package com.nekgambling.application.event.player.spin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.model.player.PlayerSpin

interface ISpinEvent : IEventAdapter.AppEvent {
    val spin: PlayerSpin
}
package com.nekgambling.application.event.player.spin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.player.model.PlayerSpin

interface ISpinEvent : IEventAdapter.AppEvent {
    val spin: PlayerSpin
}
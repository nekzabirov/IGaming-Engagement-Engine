package com.nekgambling.application.event.player.freespin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.model.player.PlayerFreespin

interface IFreespinEvent : IEventAdapter.AppEvent {
    val freespin: PlayerFreespin
}
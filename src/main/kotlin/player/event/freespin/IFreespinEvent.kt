package com.nekgambling.player.event.freespin

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.model.PlayerFreespin

interface IFreespinEvent : IEventAdapter.AppEvent {
    val freespin: PlayerFreespin
}
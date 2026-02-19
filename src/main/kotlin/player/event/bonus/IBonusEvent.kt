package com.nekgambling.player.event.bonus

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.model.PlayerBonus

interface IBonusEvent : IEventAdapter.AppEvent {
    val bonus: PlayerBonus
}
package com.nekgambling.application.event.player.bonus

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.model.player.PlayerBonus

interface IBonusEvent : IEventAdapter.AppEvent {
    val bonus: PlayerBonus
}
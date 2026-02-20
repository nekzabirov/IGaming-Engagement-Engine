package com.nekgambling.application.event.player.bonus

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.player.model.PlayerBonus

interface IBonusEvent : IEventAdapter.AppEvent {
    val bonus: PlayerBonus
}
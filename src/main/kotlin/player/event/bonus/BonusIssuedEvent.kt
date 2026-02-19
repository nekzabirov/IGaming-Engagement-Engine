package com.nekgambling.player.event.bonus

import com.nekgambling.player.model.PlayerBonus

data class BonusIssuedEvent(override val bonus: PlayerBonus) : IBonusEvent

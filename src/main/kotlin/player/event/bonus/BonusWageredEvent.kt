package com.nekgambling.player.event.bonus

import com.nekgambling.player.model.PlayerBonus

data class BonusWageredEvent(override val bonus: PlayerBonus) : IBonusEvent

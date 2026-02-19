package com.nekgambling.player.event.bonus

import com.nekgambling.player.model.PlayerBonus

data class BonusStartWageringEvent(override val bonus: PlayerBonus) : IBonusEvent

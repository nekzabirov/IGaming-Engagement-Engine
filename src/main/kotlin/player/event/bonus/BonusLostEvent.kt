package com.nekgambling.player.event.bonus

import com.nekgambling.player.model.PlayerBonus

data class BonusLostEvent(override val bonus: PlayerBonus) : IBonusEvent

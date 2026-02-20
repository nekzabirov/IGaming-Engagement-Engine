package com.nekgambling.application.event.player.bonus

import com.nekgambling.domain.player.model.PlayerBonus

data class BonusStartWageringEvent(override val bonus: PlayerBonus) : IBonusEvent

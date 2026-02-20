package com.nekgambling.application.event.player.bonus

import com.nekgambling.domain.player.model.PlayerBonus

data class BonusLostEvent(override val bonus: PlayerBonus) : IBonusEvent

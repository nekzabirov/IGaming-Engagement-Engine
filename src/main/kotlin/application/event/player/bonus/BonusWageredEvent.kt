package com.nekgambling.application.event.player.bonus

import com.nekgambling.domain.model.player.PlayerBonus

data class BonusWageredEvent(override val bonus: PlayerBonus) :
    com.nekgambling.application.event.player.bonus.IBonusEvent

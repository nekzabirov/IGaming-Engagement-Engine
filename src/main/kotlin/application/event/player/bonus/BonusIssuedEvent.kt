package com.nekgambling.application.event.player.bonus

import com.nekgambling.domain.player.model.PlayerBonus

data class BonusIssuedEvent(override val bonus: PlayerBonus) : IBonusEvent

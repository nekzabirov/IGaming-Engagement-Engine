package com.nekgambling.application.event.player.spin

import com.nekgambling.domain.player.model.PlayerSpin

data class SpinOpenedEvent(override val spin: PlayerSpin) : ISpinEvent

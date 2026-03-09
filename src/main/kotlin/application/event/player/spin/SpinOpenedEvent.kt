package com.nekgambling.application.event.player.spin

import com.nekgambling.domain.model.player.PlayerSpin

data class SpinOpenedEvent(override val spin: PlayerSpin) : com.nekgambling.application.event.player.spin.ISpinEvent

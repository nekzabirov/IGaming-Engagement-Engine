package com.nekgambling.application.event.player.spin

import com.nekgambling.domain.player.model.PlayerSpin

data class SpinClosedEvent(override val spin: PlayerSpin) : com.nekgambling.application.event.player.spin.ISpinEvent

package com.nekgambling.player.event.spin

import com.nekgambling.player.model.PlayerSpin

data class SpinClosedEvent(override val spin: PlayerSpin) : ISpinEvent

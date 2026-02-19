package com.nekgambling.player.event.spin

import com.nekgambling.player.model.PlayerSpin

data class SpinOpenedEvent(override val spin: PlayerSpin) : ISpinEvent

package com.nekgambling.player.event.freespin

import com.nekgambling.player.model.PlayerFreespin

data class FreespinIssuedEvent(override val freespin: PlayerFreespin) : IFreespinEvent

package com.nekgambling.player.event.freespin

import com.nekgambling.player.model.PlayerFreespin

data class FreespinActivatedEvent(override val freespin: PlayerFreespin) : IFreespinEvent

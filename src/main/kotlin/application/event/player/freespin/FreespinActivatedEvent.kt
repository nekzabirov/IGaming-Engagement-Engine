package com.nekgambling.application.event.player.freespin

import com.nekgambling.domain.player.model.PlayerFreespin

data class FreespinActivatedEvent(override val freespin: PlayerFreespin) :
    com.nekgambling.application.event.player.freespin.IFreespinEvent

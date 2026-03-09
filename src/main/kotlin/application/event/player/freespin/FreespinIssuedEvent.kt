package com.nekgambling.application.event.player.freespin

import com.nekgambling.domain.model.player.PlayerFreespin

data class FreespinIssuedEvent(override val freespin: PlayerFreespin) :
    com.nekgambling.application.event.player.freespin.IFreespinEvent

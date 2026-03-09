package com.nekgambling.infrastructure.external.rabbitmq.mapper

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.bonus.*
import com.nekgambling.application.event.player.freespin.*
import com.nekgambling.application.event.player.invoice.IInvoiceEvent
import com.nekgambling.application.event.player.invoice.InvoiceCreatedEvent
import com.nekgambling.application.event.player.invoice.InvoiceUpdatedEvent
import com.nekgambling.application.event.player.player.PlayerRegisteredEvent
import com.nekgambling.application.event.player.player.PlayerUpdatedEvent
import com.nekgambling.application.event.player.spin.ISpinEvent
import com.nekgambling.application.event.player.spin.SpinClosedEvent
import com.nekgambling.application.event.player.spin.SpinOpenedEvent

fun IEventAdapter.AppEvent.routingKey(): String = when (this) {
    is BonusIssuedEvent -> "bonus.issued"
    is BonusLostEvent -> "bonus.lost"
    is BonusStartWageringEvent -> "bonus.start_wagering"
    is BonusWageredEvent -> "bonus.wagered"

    is FreespinIssuedEvent -> "freespin.issued"
    is FreespinActivatedEvent -> "freespin.activated"
    is FreespinCanceledEvent -> "freespin.canceled"
    is FreespinPlayedEvent -> "freespin.played"

    is SpinOpenedEvent -> "spin.opened"
    is SpinClosedEvent -> "spin.closed"

    is InvoiceCreatedEvent -> "invoice.created"
    is InvoiceUpdatedEvent -> "invoice.updated"

    is PlayerRegisteredEvent -> "player.registered"
    is PlayerUpdatedEvent -> "player.updated"

    else -> throw IllegalArgumentException("Unknown event type: ${this::class.simpleName}")
}

fun IEventAdapter.AppEvent.toJson(): String = when (this) {
    is IBonusEvent -> bonus.toJson()
    is IFreespinEvent -> freespin.toJson()
    is ISpinEvent -> spin.toJson()
    is IInvoiceEvent -> invoice.toJson()
    is PlayerRegisteredEvent -> details.toJson(playerId)
    is PlayerUpdatedEvent -> details.toJson(playerId)
    else -> throw IllegalArgumentException("Unknown event type: ${this::class.simpleName}")
}.toString()

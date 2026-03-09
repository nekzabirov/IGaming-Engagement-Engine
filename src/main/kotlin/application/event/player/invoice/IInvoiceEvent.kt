package com.nekgambling.application.event.player.invoice

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.model.player.PlayerInvoice

interface IInvoiceEvent : IEventAdapter.AppEvent {
    val invoice: PlayerInvoice
}
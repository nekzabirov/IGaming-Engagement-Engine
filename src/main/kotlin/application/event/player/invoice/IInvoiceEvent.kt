package com.nekgambling.application.event.player.invoice

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.domain.player.model.PlayerInvoice

interface IInvoiceEvent : IEventAdapter.AppEvent {
    val invoice: PlayerInvoice
}
package com.nekgambling.player.event.invoice

import com.nekgambling.core.adapter.IEventAdapter
import com.nekgambling.player.model.PlayerInvoice

interface IInvoiceEvent : IEventAdapter.AppEvent {
    val invoice: PlayerInvoice
}
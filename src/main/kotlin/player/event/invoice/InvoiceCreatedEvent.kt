package com.nekgambling.player.event.invoice

import com.nekgambling.player.model.PlayerInvoice

class InvoiceCreatedEvent(override val invoice: PlayerInvoice) : IInvoiceEvent
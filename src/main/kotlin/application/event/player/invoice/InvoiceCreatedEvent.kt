package com.nekgambling.application.event.player.invoice

import com.nekgambling.domain.model.player.PlayerInvoice

class InvoiceCreatedEvent(override val invoice: PlayerInvoice) : IInvoiceEvent
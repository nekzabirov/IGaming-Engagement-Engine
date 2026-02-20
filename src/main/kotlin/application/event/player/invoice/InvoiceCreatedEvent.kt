package com.nekgambling.application.event.player.invoice

import com.nekgambling.domain.player.model.PlayerInvoice

class InvoiceCreatedEvent(override val invoice: PlayerInvoice) : IInvoiceEvent
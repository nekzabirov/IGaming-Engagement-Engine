package com.nekgambling.application.event.player.invoice

import com.nekgambling.domain.player.model.PlayerInvoice

class InvoiceUpdatedEvent(override val invoice: PlayerInvoice) : IInvoiceEvent
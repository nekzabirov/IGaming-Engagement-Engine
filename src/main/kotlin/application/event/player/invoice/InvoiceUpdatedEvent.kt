package com.nekgambling.application.event.player.invoice

import com.nekgambling.domain.model.player.PlayerInvoice

class InvoiceUpdatedEvent(override val invoice: PlayerInvoice) :
    com.nekgambling.application.event.player.invoice.IInvoiceEvent
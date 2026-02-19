package com.nekgambling.player.event.invoice

import com.nekgambling.player.model.PlayerInvoice

class InvoiceUpdatedEvent(override val invoice: PlayerInvoice) : IInvoiceEvent
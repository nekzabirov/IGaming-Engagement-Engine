package com.nekgambling.domain.player.repository

import com.nekgambling.domain.player.model.PlayerInvoice
import java.util.Optional

interface IPlayerInvoiceRepository {
    suspend fun findById(id: String): Optional<PlayerInvoice>

    suspend fun save(data: PlayerInvoice): PlayerInvoice
}
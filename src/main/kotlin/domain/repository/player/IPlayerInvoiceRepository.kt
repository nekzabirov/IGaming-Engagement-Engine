package com.nekgambling.domain.repository.player

import com.nekgambling.domain.model.player.PlayerInvoice
import java.util.Optional

interface IPlayerInvoiceRepository {
    suspend fun findById(id: String): Optional<PlayerInvoice>

    suspend fun save(data: PlayerInvoice): PlayerInvoice
}
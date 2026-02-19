package com.nekgambling.player.model

import com.nekgambling.core.vo.Currency
import kotlin.time.Instant

data class PlayerInvoice(
    val id: String,

    val playerId: String,

    val type: Type,

    var status: Status,

    val transactionCurrency: Currency,

    val amount: Long,
    var transactionAmount: Long,
    var taxAmount: Long,
    var feeAmount: Long,

    val createdAt: Instant,
) {
    enum class Type {
        DEPOSIT,
        PAYOUT,
        REFUND
    }

    enum class Status {
        SUCCESS,
        REJECTED,
        IN_PROGRESS
    }

    fun updateStatus(status: Status) {
        this.status = status
    }

    fun updateTaxAmount(taxAmount: Long) {
        this.taxAmount = taxAmount
    }

    fun updateTransactionAmount(transactionAmount: Long) {
        this.transactionAmount = transactionAmount
    }

    fun updateFeeAmount(feeAmount: Long) {
        this.feeAmount = feeAmount
    }
}

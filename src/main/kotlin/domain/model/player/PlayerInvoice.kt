package com.nekgambling.domain.model.player

import com.nekgambling.domain.vo.Currency
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

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
    @Serializable
    enum class Type {
        DEPOSIT,
        PAYOUT,
        REFUND
    }

    @Serializable
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

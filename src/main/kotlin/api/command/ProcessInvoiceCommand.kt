package com.nekgambling.api.command



data class ProcessInvoiceCommand(
    val playerId: String,
    val transactionId: String,
    val amount: Double,
    val date: Long,
    val paymentMethod: String,
    val currency: String,
    val type: Type,
    val status: Status,
    val transactionAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val feeAmount: Double = 0.0,
) : ICommand<Unit> {

    enum class Type {
        DEPOSIT,
        WITHDRAW,
    }

    enum class Status {
        SUCCESS,
        REJECTED,
        IN_PROGRESS,
    }
}

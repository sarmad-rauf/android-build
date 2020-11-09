package com.es.marocapp.model.responses

data class PaymentResponse(
    val arguments: List<Argument>,
    val currency: Any,
    val description: String,
    val discount: Any,
    val feeAmount: Any,
    val financialReceiptResponse: FinancialReceiptResponse,
    val masterPassTransactionId: Any,
    val responseCode: String,
    val senderBalanceafter: String?,
    val transDate: Any,
    val transTime: Any,
    val transactionId: String
)

data class Argument(
    val name: String,
    val value: String
)